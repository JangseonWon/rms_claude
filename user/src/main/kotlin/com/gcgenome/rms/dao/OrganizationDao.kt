package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Organization
import com.gcgenome.rms.tables.records.OrganizationRecord
import com.gcgenome.rms.tables.references.ORGANIZATION
import org.jooq.DSLContext
import reactor.core.publisher.Mono

interface OrganizationDao{
    fun DSLContext.insertOrganization(userId: String, organizationDto: Organization): Mono<OrganizationRecord> {
        return Mono.from(
            insertInto(ORGANIZATION)
                .set(ORGANIZATION.USER_ID, userId)
                .set(ORGANIZATION.ID, userId)
                .set(ORGANIZATION.BRANCH_ID, organizationDto.branchCode)
                .set(ORGANIZATION.BRANCH_NAME, organizationDto.branchName)
                .set(ORGANIZATION.NAME, organizationDto.name)
                .set(ORGANIZATION.REGISTRATION_NUMBER, organizationDto.registrationNumber)
                .set(ORGANIZATION.TYPE, organizationDto.type)
                .returning()
        )
    }
}