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

@Component
class UserHandler(
    val dslContext: DSLContext,
    val encoder: BCryptPasswordEncoder,
    val userHistoryHandler: UserHistoryHandler
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
                    ).then(userHistoryHandler.logUserChanges(dsl(), hostUserId, user))
            }
        })
    }

    fun insertManager(hostId: String, user: User): Mono<UserHistory>{
        return if (!isPasswordValid(user.password!!)) {
            Mono.error(IllegalArgumentException("Password does not meet the required criteria."))
        } else {
            Mono.from(dslContext.transactionPublisher { trx ->
                trx.dsl().run {
                    insertManager(user.apply { password = encoder.encode(user.password) })
                        .flatMap { manager ->
                            userHistoryHandler.logUserChanges(dsl(), hostId, manager)
                        }
                }
            })
        }
    }

    fun isPasswordValid(password: String): Boolean {
        val containsUpper = password.any { it.isUpperCase() }
        val containsLower = password.any { it.isLowerCase() }
        val containsSpecial = password.any { "!@#$%^&*()_+-=[]{}|;:',.<>?/".contains(it) }
        val isLongEnough = password.length >= 10
        return containsUpper && containsLower && containsSpecial && isLongEnough
    }
}