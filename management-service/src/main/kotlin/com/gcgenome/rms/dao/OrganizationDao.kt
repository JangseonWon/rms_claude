package com.gcgenome.rms.dao

import com.gcgenome.rms.data.OrganizationDTO
import com.gcgenome.rms.tables.references.ORGANIZATION
import com.gcgenome.rms.tables.references.SAMPLE
import org.jooq.DSLContext
import org.jooq.impl.DSL.row
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface OrganizationDao{

    fun DSLContext.insertOrganization(organization: OrganizationDTO): Mono<OrganizationDTO> {
        return Mono.from(
            insertInto(ORGANIZATION)
                .set(ORGANIZATION.ID, organization.id)
                .set(ORGANIZATION.USER_ID, organization.userId)
                .set(ORGANIZATION.NAME, organization.name)
                .set(ORGANIZATION.NURSING_NUMBER, organization.nursingNumber)
                .set(ORGANIZATION.REGISTRATION_NUMBER, organization.registrationNumber)
                .set(ORGANIZATION.TYPE, organization.type)
                .returning()
        ).map{it.into(OrganizationDTO::class.java)}
    }

    fun DSLContext.selectOrganizationByUserId(userId: String): Flux<OrganizationDTO> {
        return Flux.from(
            selectFrom(ORGANIZATION).where(ORGANIZATION.USER_ID.eq(userId))
        ).map { it.into(OrganizationDTO::class.java) }
    }

    fun DSLContext.deleteOrganizationById(organization: OrganizationDTO): Mono<OrganizationDTO> {
        return Mono.from(
            deleteFrom(ORGANIZATION)
                .where(ORGANIZATION.USER_ID.eq(organization.userId)
                    .and(ORGANIZATION.ID.eq(organization.id))
                    .and(row(ORGANIZATION.ID).notIn(
                        select(SAMPLE.ORGANIZATION_ID).from(SAMPLE)
                            .where(SAMPLE.ORGANIZATION_ID.eq(organization.id).and(SAMPLE.USER_ID.eq(organization.userId)))
                    ))
                )
                .returning()
        ).map { it.into(OrganizationDTO::class.java) }
    }

    fun DSLContext.updateOrganizationByUserId(organization: OrganizationDTO): Mono<OrganizationDTO> {
        return Mono.from(
            update(ORGANIZATION)
                .set(ORGANIZATION.NAME, organization.name)
                .set(ORGANIZATION.TYPE, organization.type)
                .set(ORGANIZATION.NURSING_NUMBER, organization.nursingNumber)
                .set(ORGANIZATION.REGISTRATION_NUMBER, organization.registrationNumber)
                .where(ORGANIZATION.ID.eq(organization.id).and(ORGANIZATION.USER_ID.eq(organization.userId)))
                .returning()
        ).map { it.into(OrganizationDTO::class.java) }
    }
}