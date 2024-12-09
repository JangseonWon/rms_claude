package com.gcgenome.rms.user

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.*
import com.gcgenome.rms.exception.UserNotFoundException
import com.gcgenome.rms.tables.pojos.User
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
): UserServiceDao, ServiceDao, UserDao, OrganizationDao, UserHistoryDao {
    fun selectUser(userId: String): Mono<UserDTO> {
        return Mono.from(dslContext.selectUserById(userId))
    }
    fun selectUsers(query: Query): Mono<Page<UserDTO>> {
        return dslContext.selectUsersWithPage(query)
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
            trx.dsl().run {
                updateUserById(user)
                    .then(
                        user.services?.takeIf { it.isNotEmpty() }?.let {
                            deleteUserServiceByUserId(user.id)
                                .thenMany(Flux.fromArray(it)
                                    .flatMap { service -> insertUserService(user.id, service.id!!) }
                                ).then()
                            } ?: Mono.empty()
                    ).then(insertUserUpdateLog(hostUserId, user))
            }
        })
    }

    fun insertManager(user: User): Mono<User>{
        return if (!isPasswordValid(user.password!!)) {
            Mono.error(IllegalArgumentException("Password does not meet the required criteria."))
        } else {
            Mono.from(dslContext.transactionPublisher { trx ->
                trx.dsl().run { insertManager(user.apply { password = encoder.encode(user.password) }) }
            })
        }
    }

    fun insertUserUpdateLog(hostUserId: String, updateUser: UserDTO): Mono<UserHistory> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            val userId = updateUser.id
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