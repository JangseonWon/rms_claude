package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Organization
import com.gcgenome.rms.data.User
import com.gcgenome.rms.tables.references.ORGANIZATION
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jooq.impl.DSL.`val`
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

    fun DSLContext.updateOrganizationNameByUserId(userDto: User): Mono<Organization> {
        return Mono.from(
            update(ORGANIZATION)
                .set(ORGANIZATION.NAME, DSL.coalesce(`val`(userDto.name), ORGANIZATION.NAME))
                .where(ORGANIZATION.ID.eq(userDto.id).and(ORGANIZATION.USER_ID.eq(userDto.id)))
                .returningResult(ORGANIZATION.ID, ORGANIZATION.NAME, ORGANIZATION.TYPE, ORGANIZATION.REGISTRATION_NUMBER, ORGANIZATION.NURSING_NUMBER)
        ).map { it.into(Organization::class.java) }
    }

}