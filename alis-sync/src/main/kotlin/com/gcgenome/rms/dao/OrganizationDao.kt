package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Organization
import com.gcgenome.rms.data.RmsOrder
import com.gcgenome.rms.tables.references.ORGANIZATION
import org.jooq.DSLContext
import reactor.core.publisher.Mono

interface OrganizationDao {
    fun DSLContext.insertOrganization(rmsOrder: RmsOrder): Mono<Organization> {
        return Mono.from(
            insertInto(ORGANIZATION)
                .set(ORGANIZATION.ID, rmsOrder.organizationId)
                .set(ORGANIZATION.NAME, rmsOrder.organizationName)
                .set(ORGANIZATION.TYPE, rmsOrder.type)
                .set(ORGANIZATION.USER_ID, rmsOrder.userId)
                .set(ORGANIZATION.REGISTRATION_NUMBER, rmsOrder.registrationNumber)
                .set(ORGANIZATION.NURSING_NUMBER, rmsOrder.nursingNumber)
                .set(ORGANIZATION.BRANCH_ID, rmsOrder.branchId)
                .set(ORGANIZATION.BRANCH_NAME, rmsOrder.branchName)
                .onDuplicateKeyIgnore()
                .returning()
        ).map { it.into(Organization::class.java) }
    }
}