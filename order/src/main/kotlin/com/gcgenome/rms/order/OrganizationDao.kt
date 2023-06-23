package com.gcgenome.rms.order

import com.gcgenome.lims.tables.records.OrganizationRecord
import com.gcgenome.lims.tables.references.ORGANIZATION
import com.gcgenome.rms.data.Organization
import org.jooq.DSLContext
import reactor.core.publisher.Mono

interface OrganizationDao {
    fun DSLContext.insertOrganization(userId: String, organization: Organization?): Mono<OrganizationRecord> =
        Mono.from(
            insertInto(ORGANIZATION)
                .columns(ORGANIZATION.ID, ORGANIZATION.NAME, ORGANIZATION.TYPE, ORGANIZATION.USER_ID, ORGANIZATION.REGISTRATION_NUMBER,
                    ORGANIZATION.NURSING_NUMBER,ORGANIZATION.BRANCH_ID, ORGANIZATION.BRANCH_NAME)
                .values(organization?.id, organization?.name, organization?.type, userId,
                    organization?.registrationNumber, organization?.nursingNumber,
                    organization?.branchCode, organization?.branchName)
                .onDuplicateKeyUpdate()
                .set(ORGANIZATION.NAME, organization?.name )
                .set(ORGANIZATION.TYPE, organization?.type )
                .set(ORGANIZATION.USER_ID, userId )
                .set(ORGANIZATION.REGISTRATION_NUMBER, organization?.registrationNumber )
                .set(ORGANIZATION.NURSING_NUMBER, organization?.nursingNumber )
                .set(ORGANIZATION.BRANCH_ID, organization?.branchCode )
                .set(ORGANIZATION.BRANCH_NAME, organization?.branchName )
                .where(ORGANIZATION.ID.eq(organization?.id ?: userId))
                .returning()
        )

    /*fun DSLContext.updateOrganizationById(userId:String, organization: Organization_): Mono<Int> =
        Mono.from(
            update(ORGANIZATION)
                .set(ORGANIZATION.ID, organization.id )
                .set(ORGANIZATION.NAME, organization.name )
                .set(ORGANIZATION.TYPE, organization.type )
                .set(ORGANIZATION.USER_ID, userId )
                .set(ORGANIZATION.REGISTRATION_NUMBER, organization.registrationNumber )
                .set(ORGANIZATION.NURSING_NUMBER, organization.nursingNumber )
                .set(ORGANIZATION.BRANCH_ID, organization.branchCode )
                .set(ORGANIZATION.BRANCH_NAME, organization.branchName )
                .where(ORGANIZATION.ID.eq(organization.id ?: userId))
        )

    fun DSLContext.countOrganization(organizationId: String?): Mono<Int> =
        Mono.from( select(DSL.count(ORGANIZATION.ID).`as`("count")).from(ORGANIZATION).where(ORGANIZATION.ID.eq(organizationId)) )
            .map { r-> r.getValue("count", Int::class.java)}*/

}