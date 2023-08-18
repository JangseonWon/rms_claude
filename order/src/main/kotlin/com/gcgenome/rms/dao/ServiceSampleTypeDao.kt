package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.records.ServiceSampleTypeRecord
import com.gcgenome.rms.tables.references.SERVICE_SAMPLE_TYPE
import org.jooq.DSLContext
import reactor.core.publisher.Mono

interface ServiceSampleTypeDao {
    fun DSLContext.selectServiceSampleTypeById(sampleTypeId: String, serviceId: String): Mono<ServiceSampleTypeRecord>{
        return Mono.from(selectFrom(SERVICE_SAMPLE_TYPE).where(SERVICE_SAMPLE_TYPE.SAMPLE_TYPE_ID.eq(sampleTypeId).and(
            SERVICE_SAMPLE_TYPE.SERVICE_ID.eq(serviceId))))
    }
}