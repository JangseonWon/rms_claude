package com.gcgenome.rms.dao

import com.gcgenome.rms.data.OrganizationDTO
import com.gcgenome.rms.data.Page
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.tables.references.ORGANIZATION
import org.jooq.DSLContext
import reactor.core.publisher.Mono

interface OrganizationDao : QueryDao{
    fun DSLContext.selectUserWithOrganizations(userId: String, query: Query): Mono<Page<OrganizationDTO>> {
        return Mono.from(
            selectPage(ORGANIZATION, query) { record ->
                record.into(OrganizationDTO::class.java)
            }
        )
    }

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

    fun DSLContext.updateOrganization(organization: OrganizationDTO): Mono<OrganizationDTO> {
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