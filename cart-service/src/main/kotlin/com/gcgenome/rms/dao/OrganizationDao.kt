package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Organization
import com.gcgenome.rms.tables.references.ORGANIZATION
import org.jooq.DSLContext
import reactor.core.publisher.Flux

interface OrganizationDao {
    fun DSLContext.selectOrganizationByUserId(userId: String): Flux<Organization> {
        return Flux.from(
            selectFrom(ORGANIZATION).where(ORGANIZATION.USER_ID.eq(userId))
        ).map { it.into(Organization::class.java) }
    }
}