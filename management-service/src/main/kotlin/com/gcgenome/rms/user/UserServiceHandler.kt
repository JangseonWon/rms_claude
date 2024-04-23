package com.gcgenome.rms.user

import com.gcgenome.rms.auth.ManagerAuthenticationHandler
import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.Service_
import com.gcgenome.rms.data.UserService
import com.gcgenome.rms.exception.OrganizationNotDeleteException
import com.gcgenome.rms.exception.OrganizationNotFoundException
import com.gcgenome.rms.exception.ServiceNotFoundException
import com.gcgenome.rms.exception.UserNotFoundException
import com.gcgenome.rms.tables.pojos.Organization
import com.gcgenome.rms.tables.pojos.Service
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL.field
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class UserServiceHandler(
    val dslContext: DSLContext,
    private val managerAuthenticationHandler: ManagerAuthenticationHandler
): UserServiceDao, ServiceDao, UserDao, OrganizationDao, SampleDao {
    fun insertUserService(authentication: UserAuthentication, userId: String, services: Array<Service_>): Flux<Service_> {

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

    fun selectUserService(userId: String, filter: Query.Companion.Filter): Flux<Service> {
        val whereClause = buildUserServiceIdOrNameWhereClause(filter)
        return Flux.from(dslContext.run {
            selectUserById(userId).switchIfEmpty(Mono.error(UserNotFoundException(userId)))
                .thenMany(selectUserByServiceIdOrName(userId, whereClause))
        })
    }

    fun deleteUserServices(authentication: UserAuthentication, userId: String, services: Array<Service_>): Mono<UserService> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                managerAuthenticationHandler.chkManager(authentication)
                    .thenMany(Flux.fromArray(services)
                        .flatMap { service -> selectUserByServiceId(userId, service.id)
                            .switchIfEmpty(Mono.error(ServiceNotFoundException(service.id)))
                            .then(deleteUserService(userId, service.id))
                        })
            }
        })
    }

    fun deleteUserOrganization(authentication: UserAuthentication, userId: String, organizationId: String): Mono<Organization> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                managerAuthenticationHandler.chkManager(authentication)
                    .then(selectUserByOrganizationId(userId, organizationId)
                    .switchIfEmpty(Mono.error(OrganizationNotFoundException())))
                    .then(deleteUserByOrganizationId(userId, organizationId))
                    .switchIfEmpty(Mono.error(OrganizationNotDeleteException()))
            }
        })
    }

    fun buildUserServiceIdOrNameWhereClause(filter: Query.Companion.Filter) : Condition {
        return field("user_service.service_id").like("%${filter.value}%").or(field("service.name").like("%${filter.value}%"))
    }
}
