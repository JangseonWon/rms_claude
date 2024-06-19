package com.gcgenome.rms.dao

import com.gcgenome.rms.data.PatchOrganization
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.tables.pojos.Organization
import com.gcgenome.rms.tables.references.ORGANIZATION
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortOrder
import org.jooq.impl.DSL.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface OrganizationDao {
    fun DSLContext.insertOrganization(userId: String, organization: Organization): Mono<Organization> {
        return Mono.from(
            insertInto(ORGANIZATION)
                .set(ORGANIZATION.ID, organization.id)
                .set(ORGANIZATION.USER_ID, userId)
                .set(ORGANIZATION.NAME, organization.name)
                .set(ORGANIZATION.TYPE, organization.type)
                .set(ORGANIZATION.REGISTRATION_NUMBER, organization.registrationNumber)
                .set(ORGANIZATION.NURSING_NUMBER,organization.nursingNumber)
                .returning()
        ).map{it.into(Organization::class.java) }
    }

    fun DSLContext.updateOrganization(patchOrganization: PatchOrganization): Mono<Organization> {
        return Mono.from(
            update(ORGANIZATION)
                .set(ORGANIZATION.NAME, coalesce(`val`(patchOrganization.name), ORGANIZATION.NAME))
                .set(ORGANIZATION.TYPE, coalesce(`val`(patchOrganization.type), ORGANIZATION.TYPE))
                .set(ORGANIZATION.REGISTRATION_NUMBER, coalesce(`val`(patchOrganization.registrationNumber), ORGANIZATION.REGISTRATION_NUMBER))
                .set(ORGANIZATION.NURSING_NUMBER,coalesce(`val`(patchOrganization.nursingNumber), ORGANIZATION.NURSING_NUMBER))
                .where(ORGANIZATION.ID.eq(patchOrganization.id))
                .returning()
        ).map{it.into(Organization::class.java) }
    }

    fun DSLContext.getOrganizationById(id: String) : Mono<Organization>{
        return Mono.from(
            selectFrom(ORGANIZATION)
                .where(ORGANIZATION.ID.eq(id)))
            .map { it.into(Organization::class.java) }
    }

    fun DSLContext.selectOrganizations(query: Query, where: Condition, userId: String): Flux<Organization> {
        val asc : SortOrder = when (query.asc) {
            true -> SortOrder.ASC
            false -> SortOrder.DESC
        }

        return Flux.from(selectFrom(ORGANIZATION).where(where.and(ORGANIZATION.USER_ID.eq(userId)))
            .orderBy(field(query.sortBy).sort(asc))
            .limit(query.size)
            .offset(query.page*query.size)
        ).map{it.into(Organization::class.java)}
    }

    fun DSLContext.selectOrganizationsCount(query: Query, where: Condition, userId: String): Mono<Int> {
        return Mono.from(selectCount().from(ORGANIZATION).where(where.and(ORGANIZATION.USER_ID.eq(userId))))
            .map { it.component1() }
    }
}