package com.gcgenome.rms.user

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.*
import com.gcgenome.rms.exception.UserNotFoundException
import com.gcgenome.rms.history.UserHistoryHandler
import com.gcgenome.rms.tables.pojos.User
import com.gcgenome.rms.tables.pojos.UserHistory
import org.jooq.DSLContext
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import kotlin.random.Random

@Component
class UserHandler(
    val dslContext: DSLContext,
    val encoder: BCryptPasswordEncoder,
    val userHistoryHandler: UserHistoryHandler
): UserServiceDao, ServiceDao, UserDao, OrganizationDao, UserHistoryDao {
    fun selectUser(userId: String): Mono<UserDTO> {
        return Mono.from(dslContext.selectUserById(userId))
    }

    fun selectUsers(userAuthentication: UserAuthentication, query: Query): Mono<Page<UserDTO>> {
        val role = userAuthentication.user.role ?: "MANAGER"
        return dslContext.selectUsersWithPage(query, role)
    }

    fun selectUserOrganizations(userId: String): Flux<OrganizationDTO> {
        return Flux.from(dslContext.run {
            selectUserById(userId).switchIfEmpty(Mono.error(UserNotFoundException(userId)))
                .thenMany(selectOrganizationByUserId(userId))
        })
    }

    fun updateUserById(userAuthentication: UserAuthentication, user: UserDTO): Mono<UserHistory> {
        val hostUserId = userAuthentication.user.id!!
        return Mono.from(dslContext.transactionPublisher { trx ->
            val dsl = trx.dsl()
            dsl.run {
                updateUserById(user)
                    .then(
                        if(user.services == null){ Mono.empty() }
                        else{
                            Mono.defer {
                                val serviceIds = user.services?.mapNotNull { it.id } ?: emptyList()
                                updateUserServiceRelationships(dsl, user.id, serviceIds)
                            }
                        }
                    )
                    .then(userHistoryHandler.logUserChanges(dsl(), hostUserId, user))
            }
        })
    }

    private fun updateUserServiceRelationships(dsl: DSLContext, userId: String, serviceIds: List<String>): Mono<Void> {
        return dsl.run {
            selectUserServices(userId)
                .collectList()
                .flatMap { existingServices ->
                    val existingServiceIds = existingServices.map { it.serviceId }
                    val servicesToDelete = existingServiceIds - serviceIds
                    val servicesToInsert = serviceIds - existingServiceIds

                    Flux.fromIterable(servicesToDelete)
                        .flatMap { serviceId ->
                            deleteUserServiceByUserIdAndServiceId(userId, serviceId!!)
                        }
                        .thenMany(
                            Flux.fromIterable(servicesToInsert)
                                .flatMap { serviceId ->
                                    insertUserService(userId, serviceId!!)
                                }
                        ).then()
                }
        }
    }

    fun insertManager(hostId: String, user: User): Mono<UserHistory>{
        return Mono.from(dslContext.transactionPublisher { trx ->
                trx.dsl().run {
                    insertManager(user.apply { password = encoder.encode(temporaryPassword()) })
                        .flatMap { manager ->
                            userHistoryHandler.logUserChanges(dsl(), hostId, manager)
                        }
                }
            }
        )
    }

    fun temporaryPassword(): String {
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