package com.gcgenome.rms.profile.user

import com.gcgenome.rms.dao.UserDao
import com.gcgenome.rms.data.UserDTO
import org.jooq.DSLContext
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class UserHandler(
    val dslContext: DSLContext,
    val encoder: BCryptPasswordEncoder,
): UserDao {

    fun selectUserById(userId: String): Mono<UserDTO> {
        return dslContext.selectUserById(userId)
    }

    fun updateUserById(user: UserDTO): Mono<UserDTO> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                if (user.password != null && user.password!!.isNotBlank()) {
                    if (!isPasswordValid(user.password!!)) {
                        return@run Mono.error<UserDTO>(
                            IllegalArgumentException("Password must be at least 10 characters long and include uppercase, lowercase, and special characters.")
                        )
                    }
                    user.password = encoder.encode(user.password)
                }
                updateUserById(user)
            }
        })
    }

    fun isPasswordValid(password: String): Boolean {
        val containsUpper = password.any { it.isUpperCase() }
        val containsLower = password.any { it.isLowerCase() }
        val containsSpecial = password.any { "!@#$%^&*()_+-=[]{}|;:',.<>?/".contains(it) }
        val isLongEnough = password.length >= 10
        return containsUpper && containsLower && containsSpecial && isLongEnough
    }
}