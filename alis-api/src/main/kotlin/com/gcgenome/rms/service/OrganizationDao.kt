package com.gcgenome.rms.service

import com.gcgenome.rms.tables.references.ORGANIZATION
import org.jooq.DSLContext
import reactor.core.publisher.Mono

interface OrganizationDao {

    fun DSLContext.selectOrganizationName(organizationId: String): Mono<String> =
        Mono.from(selectFrom(ORGANIZATION)
            .where(ORGANIZATION.USER_ID.eq(organizationId))
            .and(ORGANIZATION.ID.eq(organizationId)))
            .mapNotNull { it.get(ORGANIZATION.NAME) }
}