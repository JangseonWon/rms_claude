package com.gcgenome.rms.user

import com.gcgenome.rms.auth.ManagerAuthenticationHandler
import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.dao.ServiceDao
import com.gcgenome.rms.dao.UserDao
import com.gcgenome.rms.dao.UserServiceDao
import com.gcgenome.rms.data.*
import com.gcgenome.rms.exception.ServiceNotFoundException
import com.gcgenome.rms.exception.UserNotFoundException
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class UserServiceHandler(
    val dslContext: DSLContext,
    private val managerAuthenticationHandler: ManagerAuthenticationHandler
): UserServiceDao, ServiceDao, UserDao {
    fun insertUserService(authentication: UserAuthentication, userId: String, services: Array<Service>): Flux<Service> {

        return Flux.from(dslContext.transactionPublisher{ trx ->
                trx.dsl().run {
                    managerAuthenticationHandler.chkManager(authentication)
                        .flatMap { selectUserById(userId).switchIfEmpty(Mono.error(UserNotFoundException(userId))) }
                        .thenMany(Flux.fromArray(services)
                            .flatMap { selectServiceById(it.id).switchIfEmpty(Mono.error(ServiceNotFoundException(it.id))) }
                            .flatMap { insertUserService(userId,it.id) }
                        )
                        .thenMany(selectServiceByUserId(userId))
                }
        })
    }
}
