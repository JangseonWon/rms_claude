package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.pojos.SampleType
import com.gcgenome.rms.tables.pojos.ServiceSampleType
import com.gcgenome.rms.tables.references.SAMPLE_TYPE
import com.gcgenome.rms.tables.references.SERVICE_SAMPLE_TYPE
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL.lower
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*


interface SampleTypeDao {
    fun DSLContext.selectServiceIdSampleTypeByNameOrId(serviceId: String, name: String?): Flux<SampleType> {
        return Flux.from(
            select(SAMPLE_TYPE.ID, SAMPLE_TYPE.NAME)
                .from(SAMPLE_TYPE)
                .join(SERVICE_SAMPLE_TYPE).on(SAMPLE_TYPE.ID.eq(SERVICE_SAMPLE_TYPE.SAMPLE_TYPE_ID))
                .where(SERVICE_SAMPLE_TYPE.SERVICE_ID.eq(serviceId))
                .apply { name?.let { and(lower(SAMPLE_TYPE.NAME).like("%${it.lowercase(Locale.getDefault())}%")) } }
        ).map { it.into(SampleType::class.java) }
    }

    fun DSLContext.selectSampleTypeByNameOrdId(where: Condition): Flux<SampleType> {
        return Flux.from(
            selectFrom(SAMPLE_TYPE).where(where)
        ).map { it.into(SampleType::class.java) }
    }

    fun DSLContext.insertSampleTypeByService(serviceSampleType: ServiceSampleType): Mono<ServiceSampleType> {
        return Mono.from(
            insertInto(SERVICE_SAMPLE_TYPE)
                .set(SERVICE_SAMPLE_TYPE.SERVICE_ID, serviceSampleType.serviceId)
                .set(SERVICE_SAMPLE_TYPE.SAMPLE_TYPE_ID, serviceSampleType.sampleTypeId)
                .onDuplicateKeyIgnore()
                .returning()
        ).map { it.into(ServiceSampleType::class.java) }
    }

    fun DSLContext.deleteSampleTypeByService(serviceId: String, sampleTypeId: String): Mono<ServiceSampleType> {
        return Mono.from(
            deleteFrom(SERVICE_SAMPLE_TYPE)
                .where(SERVICE_SAMPLE_TYPE.SERVICE_ID.eq(serviceId)
                    .and(SERVICE_SAMPLE_TYPE.SAMPLE_TYPE_ID.eq(sampleTypeId)))
                .returning()
        ).map { it.into(ServiceSampleType::class.java) }
    }
}