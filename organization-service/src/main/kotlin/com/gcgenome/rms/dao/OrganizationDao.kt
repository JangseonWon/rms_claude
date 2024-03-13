package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Organization
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.tables.records.OrganizationRecord
import com.gcgenome.rms.tables.references.ORGANIZATION
import org.jooq.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface OrganizationDao {
    fun DSLContext.insertOrganization(userId: String, organization: Organization?): Mono<Organization> {
        return Mono.from(
            insertInto(ORGANIZATION)
                .set(ORGANIZATION.ID, organization?.id?: userId)
                .set(ORGANIZATION.NAME, organization?.name)
                .set(ORGANIZATION.TYPE, organization?.type)
                .set(ORGANIZATION.USER_ID, userId)
                .set(ORGANIZATION.REGISTRATION_NUMBER, organization?.registrationNumber)
                .set(ORGANIZATION.NURSING_NUMBER,organization?.nursingNumber)
                .onDuplicateKeyUpdate()
                .set(ORGANIZATION.NAME, organization?.name)
                .set(ORGANIZATION.TYPE, organization?.type)
                .set(ORGANIZATION.USER_ID, userId)
                .set(ORGANIZATION.REGISTRATION_NUMBER, organization?.registrationNumber)
                .set(ORGANIZATION.NURSING_NUMBER, organization?.nursingNumber)
                .where(ORGANIZATION.ID.eq(organization?.id ?: userId))
                .returning()
        ).map{it.into(Organization::class.java) }
    }

    fun DSLContext.selectOrganizations(query: Query, where: Condition, userId: String): Flux<Organization> {
        val order : TableField<OrganizationRecord, String?> = when (query.sortBy) {
            "id" -> ORGANIZATION.ID
            "name" -> ORGANIZATION.NAME
            else -> ORGANIZATION.ID
        }
        val asc : SortOrder = when (query.asc) {
            true -> SortOrder.ASC
            false -> SortOrder.DESC
        }
        return Flux.from(selectFrom(ORGANIZATION).where(where.and(ORGANIZATION.USER_ID.eq(userId)))
            .orderBy(order.sort(asc))
            .limit(query.size)
            .offset(query.page*query.size)
        ).map{it.into(Organization::class.java)}
    }

    fun DSLContext.selectOrganizationsCount(query: Query, where: Condition, userId: String): Mono<Int> {
        return Mono.from(selectCount().from(ORGANIZATION).where(where.and(ORGANIZATION.USER_ID.eq(userId))))
            .map { it.component1() }
    }
}