package com.gcgenome.rms.dao

import com.gcgenome.rms.data.ServiceSampleType
import com.gcgenome.rms.tables.references.SERVICE_SAMPLE_TYPE
import org.jooq.DSLContext
import reactor.core.publisher.Mono

interface ServiceSampleTypeDao {

    fun DSLContext.selectServiceSampleTypeById(serviceId: String, sampleTypeId: String) : Mono<ServiceSampleType>{
        return Mono.from(
            selectFrom(SERVICE_SAMPLE_TYPE)
                .where(SERVICE_SAMPLE_TYPE.SERVICE_ID.eq(serviceId)
                    .and(SERVICE_SAMPLE_TYPE.SAMPLE_TYPE_ID.eq(sampleTypeId)))
        ).map { it.into(ServiceSampleType::class.java) }
    }
}