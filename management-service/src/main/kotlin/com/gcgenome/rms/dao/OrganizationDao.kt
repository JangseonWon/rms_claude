package com.gcgenome.rms.dao

import com.gcgenome.rms.data.OrganizationDTO
import com.gcgenome.rms.tables.references.ORGANIZATION
import com.gcgenome.rms.tables.references.SAMPLE
import org.jooq.DSLContext
import org.jooq.impl.DSL.row
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface OrganizationDao{

    fun DSLContext.selectOrganizationByUserId(userId: String): Flux<OrganizationDTO> {
        return Flux.from(
            selectFrom(ORGANIZATION).where(ORGANIZATION.USER_ID.eq(userId))
        ).map { it.into(OrganizationDTO::class.java) }
    }
}