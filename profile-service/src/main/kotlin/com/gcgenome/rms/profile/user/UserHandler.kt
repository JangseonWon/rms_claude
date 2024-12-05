package com.gcgenome.rms.profile.user

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.dao.UserDao
import com.gcgenome.rms.dao.UserHistoryDao
import com.gcgenome.rms.data.UserDTO
import com.gcgenome.rms.data.UserHistoryDTO
import com.gcgenome.rms.tables.pojos.UserHistory
import org.jooq.DSLContext
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class UserHandler(
    val dslContext: DSLContext,
    val encoder: BCryptPasswordEncoder,
): UserDao, UserHistoryDao {

    fun selectUserById(userId: String): Mono<UserDTO> {
        return dslContext.selectUserById(userId)
    }

    fun updateUserById(userAuthentication: UserAuthentication, user: UserDTO): Mono<UserHistory> {
        val hostUserId = userAuthentication.user.id!!
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                if (user.password != null && user.password!!.isNotBlank()) {
                    if (!isPasswordValid(user.password!!)) {
                        return@run Mono.error<UserHistory>(
                            IllegalArgumentException("Password must be at least 10 characters long and include uppercase, lowercase, and special characters.")
                        )
                    }
                    user.password = encoder.encode(user.password)
                }
                updateUserById(user).then(insertUserUpdateLog(hostUserId, user))
            }
        })
    }

    fun insertUserUpdateLog(hostUserId: String, updateUser: UserDTO): Mono<UserHistory> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            val userId = updateUser.id ?: hostUserId
            trx.dsl().run {
                selectUserById(userId).flatMap { oldUser ->
                    val fieldsToCompare = listOf(
                        "name" to Pair(oldUser.name, updateUser.name),
                        "password" to Pair(oldUser.password, updateUser.password),
                        "phone_number" to Pair(oldUser.phoneNumber, updateUser.phoneNumber),
                        "branch_name" to Pair(oldUser.branchName, updateUser.branchName),
                        "branch_serial" to Pair(oldUser.branchSerial, updateUser.branchSerial),
                        "email" to Pair(oldUser.email, updateUser.email),
                        "state" to Pair(oldUser.state, updateUser.state)
                    )

                    val userHistoryEntries = fieldsToCompare.mapNotNull { (fieldName, values) ->
                        val (oldValue, newValue) = values
                        if (newValue != null && oldValue != newValue) {
                            UserHistoryDTO(
                                userId = userId,
                                changedBy = hostUserId,
                                fieldName = fieldName,
                                oldValue = oldValue,
                                newValue = newValue
                            )
                        } else null
                    }

                    Flux.fromIterable(userHistoryEntries)
                        .flatMap { insertUserHistory(it) }
                        .collectList()
                        .mapNotNull { it.lastOrNull() }
                }
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