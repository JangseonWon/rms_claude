package com.gcgenome.rms.dao

import com.gcgenome.rms.data.AlisSampleType
import com.gcgenome.rms.data.Page
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.tables.pojos.SampleType
import com.gcgenome.rms.tables.references.SAMPLE_TYPE
import com.gcgenome.rms.tables.references.SERVICE
import com.gcgenome.rms.tables.references.SERVICE_SAMPLE_TYPE
import org.jooq.DSLContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


interface SampleTypeDao : QueryDao {
    fun DSLContext.selectSampleTypesWithPage(query: Query): Mono<Page<SampleType>> {
        return Mono.from(
            selectPage(SAMPLE_TYPE, query) { record ->
                record.into(SampleType::class.java)
            }
        )
    }
    fun DSLContext.selectServiceIdSampleTypeByNameOrId(serviceId: String): Flux<SampleType> {
        return Flux.from(
            select(SAMPLE_TYPE.ID, SAMPLE_TYPE.NAME)
                .from(SAMPLE_TYPE)
                .join(SERVICE_SAMPLE_TYPE).on(SAMPLE_TYPE.ID.eq(SERVICE_SAMPLE_TYPE.SAMPLE_TYPE_ID))
                .where(SERVICE_SAMPLE_TYPE.SERVICE_ID.eq(serviceId))
        ).map { it.into(SampleType::class.java) }
    }

    fun DSLContext.upsertSampleType(alisSampleType: AlisSampleType): Mono<Int> {
        return Mono.from(
            insertInto(SAMPLE_TYPE)
                .set(SAMPLE_TYPE.ID, alisSampleType.sampleCode)
                .set(SAMPLE_TYPE.NAME, alisSampleType.sampleFullName)
                .onConflict(SERVICE.ID)
                .doUpdate()
                .set(SERVICE.NAME, alisSampleType.sampleFullName)
        )
    }
}