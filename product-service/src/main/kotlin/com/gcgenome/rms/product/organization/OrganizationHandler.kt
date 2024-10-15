package com.gcgenome.rms.product.organization

import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.Organization
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Component
class OrganizationHandler(val dslContext: DSLContext):
    OrganizationDao, ServiceDao, SampleTypeDao, OrderDao, RequestDao, PatientDao, SampleDao, SampleExtensionDao
{
    fun getOrganizations(userId: String): Flux<Organization> {
        return dslContext.selectOrganizationsByUserId(userId)
    }
}