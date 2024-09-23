package com.gcgenome.rms.dao

import com.gcgenome.rms.data.ServiceSampleTypeDTO
import com.gcgenome.rms.tables.references.SERVICE_SAMPLE_TYPE
import org.jooq.DSLContext
import reactor.core.publisher.Mono

interface ServiceSampleTypeDao {
    fun DSLContext.insertServiceSampleTypeByService(serviceSampleType: ServiceSampleTypeDTO): Mono<ServiceSampleTypeDTO> {
        return Mono.from(
            insertInto(SERVICE_SAMPLE_TYPE)
                .set(SERVICE_SAMPLE_TYPE.SERVICE_ID, serviceSampleType.serviceId)
                .set(SERVICE_SAMPLE_TYPE.SAMPLE_TYPE_ID, serviceSampleType.sampleTypeId)
                .onDuplicateKeyIgnore()
                .returning()
        ).map { it.into(ServiceSampleTypeDTO::class.java) }
    }
    fun DSLContext.deleteServiceSampleTypeById(serviceSampleType: ServiceSampleTypeDTO): Mono<ServiceSampleTypeDTO> {
        return Mono.from(
            deleteFrom(SERVICE_SAMPLE_TYPE)
                .where(SERVICE_SAMPLE_TYPE.SERVICE_ID.eq(serviceSampleType.serviceId)
                    .and(SERVICE_SAMPLE_TYPE.SAMPLE_TYPE_ID.eq(serviceSampleType.sampleTypeId)))
                .returning()
        ).map { it.into(ServiceSampleTypeDTO::class.java) }
    }
}