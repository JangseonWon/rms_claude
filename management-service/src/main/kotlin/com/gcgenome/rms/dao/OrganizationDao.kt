package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Organization_
import com.gcgenome.rms.data.User
import com.gcgenome.rms.tables.pojos.Organization
import com.gcgenome.rms.tables.references.ORGANIZATION
import com.gcgenome.rms.tables.references.SAMPLE
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jooq.impl.DSL.row
import org.jooq.impl.DSL.`val`
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface OrganizationDao{

    fun DSLContext.insertOrganization(organizationDto: Organization_): Mono<Organization_> {
        return Mono.from(
            insertInto(ORGANIZATION)
                .set(ORGANIZATION.ID, organizationDto.id)
                .set(ORGANIZATION.USER_ID, organizationDto.userId)
                .set(ORGANIZATION.NAME, organizationDto.name)
                .set(ORGANIZATION.NURSING_NUMBER, organizationDto.nursingNumber)
                .set(ORGANIZATION.REGISTRATION_NUMBER, organizationDto.registrationNumber)
                .set(ORGANIZATION.TYPE, organizationDto.type)
                .returning()
        ).map{it.into(Organization_::class.java)}
    }

    fun DSLContext.updateOrganizationNameByUserId(userDto: User): Mono<Organization_> {
        return Mono.from(
            update(ORGANIZATION)
                .set(ORGANIZATION.NAME, DSL.coalesce(`val`(userDto.name), ORGANIZATION.NAME))
                .where(ORGANIZATION.ID.eq(userDto.id).and(ORGANIZATION.USER_ID.eq(userDto.id)))
                .returningResult(ORGANIZATION.ID, ORGANIZATION.NAME, ORGANIZATION.TYPE, ORGANIZATION.REGISTRATION_NUMBER, ORGANIZATION.NURSING_NUMBER)
        ).map { it.into(Organization_::class.java) }
    }

    fun DSLContext.selectOrganizationByUserId(userId: String): Flux<Organization> {
        return Flux.from(
            selectFrom(ORGANIZATION).where(ORGANIZATION.USER_ID.eq(userId))
        ).map { it.into(Organization::class.java) }
    }

    fun DSLContext.selectUserByOrganizationId(userId: String, organizationId: String): Mono<Organization> {
        return Mono.from(
            selectFrom(ORGANIZATION).where(ORGANIZATION.USER_ID.eq(userId).and(ORGANIZATION.ID.eq(organizationId)))
        ).map { it.into(Organization::class.java) }
    }

    fun DSLContext.deleteUserByOrganizationId(userId: String, organizationId: String): Mono<Organization> {
        return Mono.from(
            deleteFrom(ORGANIZATION)
                .where(ORGANIZATION.USER_ID.eq(userId)
                    .and(ORGANIZATION.ID.eq(organizationId))
                    .and(row(ORGANIZATION.ID).notIn(
                        select(SAMPLE.ORGANIZATION_ID).from(SAMPLE)
                            .where(SAMPLE.ORGANIZATION_ID.eq(organizationId).and(SAMPLE.USER_ID.eq(userId)))
                    ))
                )
                .returning()
        ).map { it.into(Organization::class.java) }
    }

    fun DSLContext.updateOrganizationByUserId(userId: String, organizationId: String, organization: Organization): Mono<Organization> {
        return Mono.from(
            update(ORGANIZATION)
                .set(ORGANIZATION.NAME, organization.name)
                .set(ORGANIZATION.TYPE, organization.type)
                .set(ORGANIZATION.NURSING_NUMBER, organization.nursingNumber)
                .set(ORGANIZATION.REGISTRATION_NUMBER, organization.registrationNumber)
                .where(ORGANIZATION.ID.eq(organizationId).and(ORGANIZATION.USER_ID.eq(userId)))
                .returning()
        ).map { it.into(Organization::class.java) }
    }
}