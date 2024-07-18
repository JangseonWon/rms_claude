package com.gcgenome.rms.organization

import com.gcgenome.rms.dao.OrganizationDao
import com.gcgenome.rms.dao.UserDao
import com.gcgenome.rms.data.Page
import com.gcgenome.rms.data.PatchOrganization
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.exception.FilterOperatorNotFoundException
import com.gcgenome.rms.exception.OrganizationNotFoundException
import com.gcgenome.rms.tables.pojos.Organization
import com.gcgenome.rms.tables.references.ORGANIZATION
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jooq.impl.DSL.field
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class OrganizationHandler(
    val dslContext: DSLContext
): OrganizationDao, UserDao {
    fun insertOrganization(userId: String, organization: Organization) : Mono<Organization> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run { insertOrganization(userId, organization) }})
    }

    fun getOrganizationById(id: String) : Mono<Organization>{
        return dslContext.getOrganizationById(id)
            .switchIfEmpty(Mono.error(OrganizationNotFoundException()));
    }

    fun updateOrganization(patchOrganization: PatchOrganization) : Mono<Organization> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run { updateOrganization(patchOrganization) }})
    }

    fun selectOrganizations(userId: String, query: Query): Mono<Page<Organization>> {
        val filters = query.filters ?: emptyList()
        val whereClause = buildWhereClause(filters)
        return dslContext.dsl().run {
            selectUserById(userId).flatMap { user ->
                val adjustedWhereClause = if (user.role == "USER") {
                    whereClause.and(ORGANIZATION.USER_ID.eq(userId))
                } else {
                    whereClause
                }
                val organization = dslContext.dsl().selectOrganizations(query, adjustedWhereClause)
                selectOrganizationsCount(adjustedWhereClause)
                    .flatMap { totalCount ->
                        var totalPage = totalCount / query.size
                        if (totalCount % query.size != 0) totalPage++
                        organization.collectList().flatMap { list ->
                            val page = Page(totalCount, totalPage, query.size, query.page + 1, list)
                            Mono.just(page)
                        }
                    }
            }
        }
    }

    fun buildWhereClause(filters:List<Query.Companion.Filter>?) : Condition {
        return filters?.let {
            it.filter { filter -> filter.key != null && filter.value?.isNotBlank() == true }
                .map { filter ->
                    val key = filter.key!!
                    val value = filter.value!!
                    val field = when (key) {
                        "user_id" -> ORGANIZATION.USER_ID
                        "name" -> ORGANIZATION.NAME
                        "type" -> ORGANIZATION.TYPE
                        "nursing_number" -> ORGANIZATION.NURSING_NUMBER
                        "registration_number" -> ORGANIZATION.REGISTRATION_NUMBER
                        else -> throw IllegalArgumentException("Unknown filter key: $key")
                    }
                    val condition = when (filter.operator) {
                        "=" -> field(key).eq(value)
                        "LIKE" -> field.likeIgnoreCase("%$value%")
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
}