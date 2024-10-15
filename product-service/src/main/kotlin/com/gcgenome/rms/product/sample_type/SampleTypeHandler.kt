package com.gcgenome.rms.product.sample_type

import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.SampleType
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Component
class SampleTypeHandler(val dslContext: DSLContext):
    OrganizationDao, ServiceDao, SampleTypeDao, OrderDao, RequestDao, PatientDao, SampleDao, SampleExtensionDao
{
    fun getSampleTypes(serviceId: String): Flux<SampleType> {
        return dslContext.selectSampleTypeByServicId(serviceId)
    }
}