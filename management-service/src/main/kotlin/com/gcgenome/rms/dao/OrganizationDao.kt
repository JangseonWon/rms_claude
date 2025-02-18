package com.gcgenome.rms.dao

import com.gcgenome.rms.data.AlisOrganization
import com.gcgenome.rms.data.OrganizationDTO
import com.gcgenome.rms.tables.references.ORGANIZATION
import org.jooq.DSLContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface OrganizationDao{
    fun DSLContext.insertOrganization(alisOrganizationDTO: AlisOrganization): Mono<OrganizationDTO> {
        return Mono.from(
            insertInto(ORGANIZATION)
                .set(ORGANIZATION.ID, alisOrganizationDTO.compCode)
                .set(ORGANIZATION.USER_ID, alisOrganizationDTO.compCode)
                .set(ORGANIZATION.NAME, alisOrganizationDTO.compName)
                .returning()
        ).map { it.into(OrganizationDTO::class.java) }
    }

    fun DSLContext.selectOrganizationByUserId(userId: String): Flux<OrganizationDTO> {
        return Flux.from(
            selectFrom(ORGANIZATION).where(ORGANIZATION.USER_ID.eq(userId))
        ).map { it.into(OrganizationDTO::class.java) }
    }
}