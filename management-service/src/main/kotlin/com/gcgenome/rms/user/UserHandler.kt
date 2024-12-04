package com.gcgenome.rms.user

import com.gcgenome.rms.dao.OrganizationDao
import com.gcgenome.rms.dao.ServiceDao
import com.gcgenome.rms.dao.UserDao
import com.gcgenome.rms.dao.UserServiceDao
import com.gcgenome.rms.data.OrganizationDTO
import com.gcgenome.rms.data.Page
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.UserDTO
import com.gcgenome.rms.exception.UserNotFoundException
import com.gcgenome.rms.tables.pojos.User
import org.jooq.DSLContext
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class UserHandler(
    val dslContext: DSLContext,
    val encoder: BCryptPasswordEncoder,
): UserServiceDao, ServiceDao, UserDao, OrganizationDao {
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

    fun updateUserById(user: UserDTO): Mono<Void> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                updateUserById(user)
                    .then<Void?>(
                        user.services?.takeIf { it.isNotEmpty() }?.let {
                            deleteUserServiceByUserId(user.id)
                                .thenMany(Flux.fromArray(it)
                                    .flatMap { service -> insertUserService(user.id, service.id!!) }
                                ).then()
                            } ?: Mono.empty()
                    )

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

    fun isPasswordValid(password: String): Boolean {
        val containsUpper = password.any { it.isUpperCase() }
        val containsLower = password.any { it.isLowerCase() }
        val containsSpecial = password.any { "!@#$%^&*()_+-=[]{}|;:',.<>?/".contains(it) }
        val isLongEnough = password.length >= 10
        return containsUpper && containsLower && containsSpecial && isLongEnough
    }
}