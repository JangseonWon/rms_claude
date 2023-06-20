package com.gcgenome.rms.order

import com.gcgenome.lims.tables.records.SampleExtensionRecord
import com.gcgenome.lims.tables.references.SAMPLE_EXTENSION
import com.gcgenome.rms.data.Extension_
import org.jooq.DSLContext
import org.jooq.impl.DSL
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.util.*

interface ExtensionDao{
    fun DSLContext.insertSampleExtension(sampleExtension: Extension_, sampleId: UUID): Mono<SampleExtensionRecord> =
        Mono.from(
            insertInto(SAMPLE_EXTENSION)
            .columns(SAMPLE_EXTENSION.EXTENSION_ID, SAMPLE_EXTENSION.SAMPLE_ID, SAMPLE_EXTENSION.VALUE)
            .values(sampleExtension.id, sampleId, sampleExtension.value)
            .returning()
        )

    fun DSLContext.deleteSampleExtensionBySampleId(sampleId:UUID): Mono<SampleExtensionRecord> =
        Mono.from(deleteFrom(SAMPLE_EXTENSION).where(SAMPLE_EXTENSION.SAMPLE_ID.eq(sampleId)).returning())

    fun DSLContext.countExtension(sampleId: UUID): Mono<Int> =
        Mono.from(
            select(DSL.count(SAMPLE_EXTENSION.SAMPLE_ID).`as`("count"))
                .from(SAMPLE_EXTENSION).where(SAMPLE_EXTENSION.SAMPLE_ID.eq(sampleId))
        ).map { r -> r.getValue("count", Int::class.java) }

    fun DSLContext.deleteExtension(sampleId: UUID) =
        deleteFrom(SAMPLE_EXTENSION).where(SAMPLE_EXTENSION.SAMPLE_ID.eq(sampleId)).toMono()
}