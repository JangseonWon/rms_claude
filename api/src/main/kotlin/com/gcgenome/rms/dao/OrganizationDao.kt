package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.references.ORGANIZATION
import com.gcgenome.rms.data.Organization
import org.jooq.DSLContext
import reactor.core.publisher.Mono

interface OrganizationDao {
    fun DSLContext.insertOrganization(userId: String, organization: Organization?): Mono<Organization> {
        return Mono.from(
            insertInto(ORGANIZATION)
                .set(ORGANIZATION.ID, organization?.id?: userId)
                .set(ORGANIZATION.NAME, organization?.name)
                .set(ORGANIZATION.TYPE, organization?.type)
                .set(ORGANIZATION.USER_ID, userId)
                .set(ORGANIZATION.REGISTRATION_NUMBER, organization?.registrationNumber)
                .set(ORGANIZATION.NURSING_NUMBER,organization?.nursingNumber)
                .set(ORGANIZATION.BRANCH_ID, organization?.branchCode)
                .set(ORGANIZATION.BRANCH_NAME, organization?.branchName)
                .onDuplicateKeyUpdate()
                .set(ORGANIZATION.NAME, organization?.name)
                .set(ORGANIZATION.TYPE, organization?.type)
                .set(ORGANIZATION.USER_ID, userId)
                .set(ORGANIZATION.REGISTRATION_NUMBER, organization?.registrationNumber)
                .set(ORGANIZATION.NURSING_NUMBER, organization?.nursingNumber)
                .set(ORGANIZATION.BRANCH_ID, organization?.branchCode)
                .set(ORGANIZATION.BRANCH_NAME, organization?.branchName)
                .where(ORGANIZATION.ID.eq(organization?.id ?: userId))
                .returning()
        ).map { it.into(Organization::class.java) }
    }
}