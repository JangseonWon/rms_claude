package com.gcgenome.rms.dao

import com.gcgenome.lims.tables.records.OrganizationServiceRecord
import com.gcgenome.lims.tables.references.ORGANIZATION_SERVICE
import org.jooq.DSLContext
import reactor.core.publisher.Mono

interface OrganizationServiceDao {

    fun DSLContext.insertOrganizationService(organizationId: String, serviceId: String, userId: String): Mono<OrganizationServiceRecord> =
        Mono.from(
            insertInto(ORGANIZATION_SERVICE)
                .columns(ORGANIZATION_SERVICE.ORGANIZATION_ID, ORGANIZATION_SERVICE.SERVICE_ID, ORGANIZATION_SERVICE.USER_ID)
                .values(organizationId, serviceId, userId)
                .returning()
        )
}