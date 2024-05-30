package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Organization
import com.gcgenome.rms.tables.records.OrganizationRecord
import com.gcgenome.rms.tables.references.ORGANIZATION
import org.jooq.DSLContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface OrganizationDao {
    fun DSLContext.selectOrganizationsByUserId(userId: String): Flux<Organization> {
        return Flux.from(
            selectFrom(ORGANIZATION).where(ORGANIZATION.USER_ID.eq(userId))
        ).map { it.into(Organization::class.java) }
    }
    fun DSLContext.selectOrganization(userId: String, organizationId: String): Mono<OrganizationRecord> {
        return Mono.from(
            selectFrom(ORGANIZATION)
                .where(ORGANIZATION.ID.eq(organizationId).and(ORGANIZATION.USER_ID.eq(userId)))
        )
    }
}