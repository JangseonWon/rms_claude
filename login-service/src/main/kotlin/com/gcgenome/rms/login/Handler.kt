package com.gcgenome.rms.login

import com.gcgenome.rms.authenticate.TokenFactory
import com.gcgenome.rms.dao.UserDao
import com.gcgenome.rms.exceptions.UserNotFoundException
import com.gcgenome.rms.tables.pojos.User
import org.jooq.DSLContext
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import kotlin.random.Random

@Service
class Handler(
    val dslContext: DSLContext,
    val encoder: BCryptPasswordEncoder,
    val token: TokenFactory,
    val mailSender: JavaMailSender
):UserDao {
    fun login(user: User): Mono<String>{
        return dslContext.dsl().selectUserById(user.id!!)
            .filter { encoder.matches(user.password, it.password) }
            .switchIfEmpty(Mono.error(UserNotFoundException()))
            .map(token::publish)
    }

    fun signup(user: User): Mono<User>{
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run { insertUser(user.apply { password = encoder.encode(user.password) }) }
        })
    }

    fun temporaryPassword(user: User): Mono<String> {
        val generatedPassword = generatePassword()
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run { checkUserByIdAndMail(user)
                .switchIfEmpty(Mono.error(UserNotFoundException()))
                .flatMap { changePasswordByUserId(it.id!!, password = encoder.encode(generatedPassword)) }
                .then(sendEmail(user.email!!, generatedPassword)) }
        })
    }

    fun sendEmail(to: String, password: String): Mono<String> {
        return try {
            val message = SimpleMailMessage()
            message.setTo(to)
            message.subject = "We will issue you a temporary password for GCGenome."
            message.text = "Here is your temporary password: $password"
            message.from = "noreply@gcgenome.com"
            mailSender.send(message)

            Mono.just("Email sent successfully to $to")
        } catch (e: Exception) {
            Mono.error(Exception("Failed to send email to $to"))
        }
    }

    fun generatePassword(): String {
        val minLength = 8
        val maxLength = 20

        val upperCaseChars = ('A'..'Z').toList()
        val lowerCaseChars = ('a'..'z').toList()
        val digitChars = ('0'..'9').toList()
        val specialChars = listOf('@', '#', '$', '%', '^', '&', '*')
        val allChars = upperCaseChars + lowerCaseChars + digitChars + specialChars

        fun containsUpper(password: String) = password.any { it in upperCaseChars }
        fun containsLower(password: String) = password.any { it in lowerCaseChars }
        fun containsDigit(password: String) = password.any { it in digitChars }
        fun containsSpecial(password: String) = password.any { it in specialChars }
        fun hasConsecutiveChars(password: String): Boolean {
            for (i in 0 until password.length - 2) {
                if (password[i] == password[i + 1] && password[i] == password[i + 2]) {
                    return true
                }
            }
            return false
        }

        fun containsCommonWords(password: String): Boolean {
            val commonWords = listOf("password", "admin", "welcome", "123456", "qwerty")
            return commonWords.any { password.contains(it, ignoreCase = true) }
        }

        fun isValid(password: String): Boolean {
            return password.length in minLength..maxLength &&
                    containsUpper(password) &&
                    containsLower(password) &&
                    containsDigit(password) &&
                    containsSpecial(password) &&
                    !hasConsecutiveChars(password) &&
                    !containsCommonWords(password) &&
                    !password.contains(" ")
        }

        fun generateRandomPassword(): String {
            val passwordLength = Random.nextInt(minLength, maxLength + 1)
            return List(passwordLength) { allChars.random() }.joinToString("")
        }

        var password: String
        do {
            password = generateRandomPassword()
        } while (!isValid(password))

        return password
    }
}