package com.gcgenome.rms.organization

import com.gcgenome.rms.dao.OrganizationDao
import com.gcgenome.rms.data.Organization
import com.gcgenome.rms.data.Page
import com.gcgenome.rms.data.Query
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jooq.impl.DSL.field
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class OrganizationHandler(
    val dslContext: DSLContext
): OrganizationDao {
    fun insertOrganization(userId: String, organization: Organization) : Mono<Organization> {
        return dslContext.insertOrganization(userId, organization)
    }

    fun selectOrganizations(userId: String, query: Query): Mono<Page<Organization>> {
        val filters = query.filters ?: emptyList()
        val whereClause = buildSelectUserOrganizationWhereClause(filters)
        val organization = dslContext.dsl().selectOrganizations(query, whereClause, userId)
        return dslContext.selectOrganizationsCount(query, whereClause, userId)
            .flatMap { totalCount ->
                var totalPage = totalCount / query.size
                if (totalCount % query.size != 0) totalPage++
                organization.collectList().flatMap { list ->
                    val page = Page(totalCount, totalPage, query.size, query.page + 1, list)
                    Mono.just(page)
                }
            }
    }

    fun buildSelectUserOrganizationWhereClause(filters: List<Query.Companion.Filter>): Condition {
        val conditions = filters.map { filter ->
            val key = filter.key
            val value = filter.value
            when (key) {
                "name", "registration_number" -> {
                    value?.takeIf { it.isNotBlank() }?.let {
                        field(key).like("%$it%") as Condition?
                    }
                }
                else -> throw IllegalArgumentException("제공하지 않는 컬럼 명: $key")
            }
        }

        return if (conditions.isNotEmpty()) {
            conditions.reduceOrNull { acc, condition -> acc?.and(condition) ?: condition }
                ?: DSL.trueCondition()
        } else {
            DSL.trueCondition()
        }
    }
}