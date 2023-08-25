package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.records.OrganizationRecord
import com.gcgenome.rms.tables.references.ORGANIZATION
import com.gcgenome.rms.data.Organization
import org.jooq.DSLContext
import reactor.core.publisher.Mono

interface OrganizationDao {

    fun DSLContext.insertOrganization(userId: String, organization: Organization?): Mono<OrganizationRecord> =
        Mono.from(
            insertInto(ORGANIZATION)
                .columns(ORGANIZATION.ID, ORGANIZATION.NAME, ORGANIZATION.TYPE, ORGANIZATION.USER_ID, ORGANIZATION.REGISTRATION_NUMBER,
                    ORGANIZATION.NURSING_NUMBER,ORGANIZATION.BRANCH_ID, ORGANIZATION.BRANCH_NAME)
                .values(organization?.id, organization?.name, organization?.type, organization?.userId,
                    organization?.registrationNumber, organization?.nursingNumber,
                    organization?.branchCode, organization?.branchName)
                .returning()
        )

    fun DSLContext.deleteOrganization(userId: String): Mono<OrganizationRecord> =
        Mono.from(deleteFrom(ORGANIZATION).where(ORGANIZATION.USER_ID.eq(userId)).returning())
}