package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Organization
import com.gcgenome.rms.tables.references.ORGANIZATION
import org.jooq.DSLContext
import reactor.core.publisher.Mono

interface OrganizationDao{

    fun DSLContext.insertOrganization(organizationDto: Organization): Mono<Organization> {
        return Mono.from(
            insertInto(ORGANIZATION)
                .set(ORGANIZATION.ID, organizationDto.id)
                .set(ORGANIZATION.USER_ID, organizationDto.userId)
                .set(ORGANIZATION.NAME, organizationDto.name)
                .set(ORGANIZATION.NURSING_NUMBER, organizationDto.nursingNumber)
                .set(ORGANIZATION.REGISTRATION_NUMBER, organizationDto.registrationNumber)
                .set(ORGANIZATION.TYPE, organizationDto.type)
                .returning()
        ).map{it.into(Organization::class.java)}
    }

}