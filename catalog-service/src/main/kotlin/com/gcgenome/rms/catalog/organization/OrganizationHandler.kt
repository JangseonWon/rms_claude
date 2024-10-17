package com.gcgenome.rms.catalog.organization

import com.gcgenome.rms.dao.OrganizationDao
import com.gcgenome.rms.tables.pojos.Organization
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Component
class OrganizationHandler(
    val dslContext: DSLContext,
): OrganizationDao {
    fun getOrganizations(userId: String): Flux<Organization> {
        return dslContext.selectOrganizationsByUserId(userId)
    }
}