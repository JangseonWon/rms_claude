package com.gcgenome.rms.dao

import com.gcgenome.rms.data.AlisSampleType
import com.gcgenome.rms.data.Page
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.SampleTypeDTO
import com.gcgenome.rms.tables.pojos.SampleType
import com.gcgenome.rms.tables.references.SAMPLE_TYPE
import com.gcgenome.rms.tables.references.SERVICE
import com.gcgenome.rms.tables.references.SERVICE_SAMPLE_TYPE
import org.jooq.DSLContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


interface SampleTypeDao : QueryDao {
    fun DSLContext.selectSampleTypeById(sampleTypeId: String): Mono<SampleTypeDTO> {
        return Mono.from(
            selectFrom(SAMPLE_TYPE)
                .where(SAMPLE_TYPE.ID.eq(sampleTypeId))
        ).map { it.into(SampleTypeDTO::class.java) }
    }
    fun DSLContext.selectSampleTypesWithPage(query: Query): Mono<Page<SampleTypeDTO>> {
        return Mono.from(
            selectPage(SAMPLE_TYPE, query) { record ->
                record.into(SampleTypeDTO::class.java)
            }
        )
    }
    fun DSLContext.updateSampleTypeById(sampleType: SampleTypeDTO): Mono<SampleTypeDTO> {
        return Mono.from(
            update(SAMPLE_TYPE)
                .set(SAMPLE_TYPE.NAME, sampleType.name)
                .where(SAMPLE_TYPE.ID.eq(sampleType.id))
                .returning()
        ).map { it.into(SampleTypeDTO::class.java) }
    }

    fun DSLContext.upsertSampleType(alisSampleType: AlisSampleType): Mono<Int> {
        return Mono.from(
            insertInto(SAMPLE_TYPE)
                .set(SAMPLE_TYPE.ID, alisSampleType.sampleCode)
                .set(SAMPLE_TYPE.NAME_KR, alisSampleType.sampleFullName)
                .onConflict(SERVICE.ID)
                .doUpdate()
                .set(SERVICE.NAME_KR, alisSampleType.sampleFullName)
        )
    }
}