package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.pojos.Organization
import com.gcgenome.rms.tables.references.ORGANIZATION
import org.jooq.Condition
import org.jooq.DSLContext
import reactor.core.publisher.Flux

interface OrganizationDao {
    fun DSLContext.selectOrganizationsByUserId(andWhere: Condition): Flux<Organization> {
        return Flux.from(
            selectFrom(ORGANIZATION).where(andWhere)
        ).map { it.into(Organization::class.java) }
    }
}