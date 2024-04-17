package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Organization
import com.gcgenome.rms.tables.references.ORGANIZATION
import org.jooq.DSLContext
import reactor.core.publisher.Mono

interface OrganizationDao {
    fun DSLContext.insertOrganization(userId: String, organization: Organization):Mono<Organization>{
        return Mono.from(
            insertInto(ORGANIZATION)
                .set(ORGANIZATION.ID,organization.id)
                .set(ORGANIZATION.USER_ID,userId)
                .set(ORGANIZATION.NAME,organization.name)
                .set(ORGANIZATION.REGISTRATION_NUMBER,organization.registrationNumber)
                .set(ORGANIZATION.TYPE,organization.type)
                .set(ORGANIZATION.NURSING_NUMBER,organization.nursingNumber)
                .onConflictDoNothing()
                .returning()
        ).map { it.into(Organization::class.java) }
    }
}