package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Organization
import com.gcgenome.rms.tables.records.OrganizationRecord
import com.gcgenome.rms.tables.references.ORGANIZATION
import org.jooq.DSLContext
import reactor.core.publisher.Mono

interface OrganizationDao {
    fun DSLContext.insertOrganization(userId: String, organization: Organization?): Mono<OrganizationRecord> {
        return Mono.from(
            insertInto(ORGANIZATION)
                .set(ORGANIZATION.ID, organization?.id?: userId)
                .set(ORGANIZATION.NAME, organization!!.name)
                .set(ORGANIZATION.TYPE, organization.type)
                .set(ORGANIZATION.USER_ID, userId)
                .set(ORGANIZATION.REGISTRATION_NUMBER, organization.registrationNumber)
                .set(ORGANIZATION.NURSING_NUMBER,organization.nursingNumber)
                .onDuplicateKeyUpdate()
                .set(ORGANIZATION.NAME, organization.name)
                .set(ORGANIZATION.TYPE, organization.type)
                .set(ORGANIZATION.USER_ID, userId)
                .set(ORGANIZATION.REGISTRATION_NUMBER, organization.registrationNumber)
                .set(ORGANIZATION.NURSING_NUMBER, organization.nursingNumber)
                .where(ORGANIZATION.ID.eq(organization.id ?: userId))
                .returning()
        )
    }

    fun DSLContext.selectOrganization(userId: String, organizationId: String): Mono<OrganizationRecord> {
        return Mono.from(
            selectFrom(ORGANIZATION)
                .where(ORGANIZATION.ID.eq(organizationId).and(ORGANIZATION.USER_ID.eq(userId)))
        )
    }
}