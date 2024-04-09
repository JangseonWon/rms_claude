package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Extension
import com.gcgenome.rms.tables.pojos.SampleExtension
import com.gcgenome.rms.tables.records.SampleExtensionRecord
import com.gcgenome.rms.tables.references.SAMPLE_EXTENSION
import org.jooq.DSLContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface SampleExtensionDao{

    fun DSLContext.insertSampleExtension(sampleExtension: Extension, sampleId: UUID): Flux<SampleExtensionRecord> {
        return Flux.from(
            insertInto(SAMPLE_EXTENSION)
                .set(SAMPLE_EXTENSION.EXTENSION_ID, sampleExtension.id)
                .set(SAMPLE_EXTENSION.SAMPLE_ID, sampleId)
                .set(SAMPLE_EXTENSION.VALUE, sampleExtension.value)
                .returning()
        )
    }

    fun DSLContext.selectSampleExtensions(sampleId: UUID) : Flux<SampleExtension> {
        return Flux.from(
            selectFrom(SAMPLE_EXTENSION)
                .where(SAMPLE_EXTENSION.SAMPLE_ID.eq(sampleId))
        ).map { it.into(SampleExtension::class.java) }
    }

    fun DSLContext.deleteSampleExtensionBySampleId(sampleId: UUID): Mono<SampleExtension> {
        return Mono.from(
            deleteFrom(SAMPLE_EXTENSION)
                .where(SAMPLE_EXTENSION.SAMPLE_ID.eq(sampleId))
                .returning()
        ).map { it.into(SampleExtension::class.java) }
    }
}