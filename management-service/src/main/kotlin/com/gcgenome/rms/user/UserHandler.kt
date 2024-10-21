package com.gcgenome.rms.user

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.dao.OrganizationDao
import com.gcgenome.rms.dao.ServiceDao
import com.gcgenome.rms.dao.UserDao
import com.gcgenome.rms.dao.UserServiceDao
import com.gcgenome.rms.data.*
import com.gcgenome.rms.exception.*
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

    fun selectUsers(query: Query): Mono<Page<UserDTO>> {
        return dslContext.selectUsersWithPage(query)
    }
    fun selectUserWithServices(userId: String, query: Query): Mono<UserDTO> {
        return dslContext.selectUserWithServicesQuery(userId, query)
    }

    fun selectUserOrganizations(userId: String): Flux<OrganizationDTO> {
        return Flux.from(dslContext.run {
            selectUserById(userId).switchIfEmpty(Mono.error(UserNotFoundException(userId)))
                .thenMany(selectOrganizationByUserId(userId))
        })
    }

    fun updateUserById(authentication: UserAuthentication, user: UserDTO): Mono<UserDTO> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                if (isAuthorized(authentication, user)) {
                    user.password = user.password?.takeIf { it.isNotBlank() }?.let { encoder.encode(it) }
                    updateUserById(user)
                } else {
                    Mono.error(ManagerAuthenticationException())
                }
            }
        })
    }
    private fun isAuthorized(authentication: UserAuthentication, user: UserDTO): Boolean {
        return authentication.user.role in listOf(Role.ADMIN.toString(), Role.MANAGER.toString()) ||
                authentication.user.id == user.id
    }

    fun insertUserService(userService: UserServiceDTO): Mono<UserDTO> {
        return Mono.from(dslContext.transactionPublisher{ trx ->
            trx.dsl().run {
                selectUserById(userService.userId!!)
                    .switchIfEmpty(Mono.error(UserNotFoundException(userService.userId!!)))
                    .flatMap {
                        selectServiceById(userService.serviceId!!)
                            .switchIfEmpty(Mono.error(ServiceNotFoundException(userService.serviceId!!)))
                    }.flatMap { insertUserService(userService) }
                    .then(selectUserWithServicesQuery(userService.userId!!, Query()))
            }
        })
    }

    fun deleteUserService(userService: UserServiceDTO): Mono<UserServiceDTO> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                selectUserServiceById(userService)
                        .switchIfEmpty(Mono.error(ServiceNotFoundException(userService.serviceId!!)))
                        .then(deleteUserService(userService))
            }
        })
    }
}