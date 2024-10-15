package com.gcgenome.rms.product.service

import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.Service
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import java.util.*

@Component
class ServiceHandler(val dslContext: DSLContext):
    OrganizationDao, ServiceDao, SampleTypeDao, OrderDao, RequestDao, PatientDao, SampleDao, SampleExtensionDao
{
    fun getServices(userId: String, categoryId: UUID): Flux<Service> {
        return dslContext.selectServiceByUserIdAndCategoryId(userId, categoryId)
    }
}