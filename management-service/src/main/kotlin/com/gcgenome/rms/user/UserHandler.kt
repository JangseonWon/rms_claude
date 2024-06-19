package com.gcgenome.rms.user

import com.gcgenome.rms.auth.ManagerAuthenticationHandler
import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.dao.OrganizationDao
import com.gcgenome.rms.dao.UserDao
import com.gcgenome.rms.data.*
import com.gcgenome.rms.exception.*
import com.gcgenome.rms.tables.pojos.Organization
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jooq.impl.DSL.field
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class UserHandler(
    val dslContext: DSLContext,
    val encoder: BCryptPasswordEncoder,
    private val managerAuthenticationHandler: ManagerAuthenticationHandler
): UserDao, OrganizationDao {

    fun selectUserById(userId: String): Mono<User> {
        return Mono.from(dslContext.selectUserById(userId))
    }

    fun selectUsers(authentication: UserAuthentication, query: Query): Mono<Page<User>> {
        val whereClause = buildWhereClause(query.filters)
        return managerAuthenticationHandler.chkManager(authentication)
            .flatMap {
                val users = dslContext.dsl().selectUsers(query,whereClause)
                dslContext.selectUsersCount(query,whereClause)
                    .flatMap { totalCount ->
                        val totalPage = (totalCount + query.size -1) / query.size
                        users.collectList().flatMap {
                            val page = Page(totalCount,totalPage,query.size,query.page+1,it)
                            Mono.just(page)
                        }
                    }
            }
    }

    fun selectUserOrganizations(userId: String): Flux<Organization> {
        return Flux.from(dslContext.run {
            selectUserById(userId).switchIfEmpty(Mono.error(UserNotFoundException(userId)))
                .thenMany(selectOrganizationByUserId(userId))
        })
    }

    fun buildWhereClause(filters:List<Query.Companion.Filter>?) : Condition {
        return filters?.let {
            it.filter { filter -> filter.key != null && filter.value?.isNotBlank() == true }
                .map { filter ->
                    val key = filter.key!!
                    val value = filter.value!!
                    val condition = when (filter.operator) {
                        "=" -> field(key).eq(value)
                        "LIKE" -> field(key).like("%$value%")
                        ">" -> field(key).gt(value)
                        "<" -> field(key).lt(value)
                        ">=" -> field(key).ge(value)
                        "<=" -> field(key).le(value)
                        else -> throw FilterOperatorNotFoundException()
                    }
                    condition
                }
                .reduceOrNull { acc, condition -> acc.and(condition) ?: condition } ?: DSL.trueCondition()
        } ?: DSL.trueCondition()
    }

    fun insertUser(authentication: UserAuthentication, userDto: User): Mono<User> {
        val password = encoder.encode(userDto.password!!)
        userDto.organization = Organization_(userDto.id,userDto.id, userDto.name,null,null,null)
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                managerAuthenticationHandler.chkManager(authentication)
                    .flatMap { insertUser(userDto, password) }
                    .flatMap { insertOrganization(userDto.organization!!) }
                    .flatMap { selectUserOrganizationById(userDto) }
            }.map { it }
        })
    }
    fun updateUserById(authentication: UserAuthentication, user: User): Mono<User> {
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
    private fun isAuthorized(authentication: UserAuthentication, user: User): Boolean {
        return authentication.user.role in listOf(Role.ADMIN.toString(), Role.MANAGER.toString()) ||
                authentication.user.id == user.id
    }
    fun insertUserOrganization(userId: String, authentication: UserAuthentication, organization: Organization): Mono<Organization_> {
        val organizationDto = Organization_(
            id = organization.id!!,
            userId = userId,
            name = organization.name,
            type = organization.type,
            registrationNumber = organization.registrationNumber,
            nursingNumber = organization.nursingNumber
        )
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                managerAuthenticationHandler.chkUserAndManager(authentication, userId)
                    .flatMap { selectUserById(userId).switchIfEmpty(Mono.error(UserNotFoundException(userId))) }
                    .flatMap { insertOrganization(organizationDto) }
            }
        })
    }

    fun deleteUserOrganization(authentication: UserAuthentication, userId: String, organizationId: String): Mono<Organization> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                managerAuthenticationHandler.chkUserAndManager(authentication, userId)
                    .then(selectUserByOrganizationId(userId, organizationId)
                        .switchIfEmpty(Mono.error(OrganizationNotFoundException())))
                    .then(deleteUserByOrganizationId(userId, organizationId))
                    .switchIfEmpty(Mono.error(OrganizationNotDeleteException()))
            }
        })
    }

    fun updateUserOrganization(authentication: UserAuthentication, userId: String, organizationId: String, organization: Organization): Mono<Organization> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                managerAuthenticationHandler.chkUserAndManager(authentication, userId)
                    .then(selectUserByOrganizationId(userId, organizationId)
                        .switchIfEmpty(Mono.error(OrganizationNotFoundException())))
                    .then(updateOrganizationByUserId(userId, organizationId, organization))
            }
        })
    }
}