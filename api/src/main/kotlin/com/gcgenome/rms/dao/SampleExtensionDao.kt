package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.references.SAMPLE_EXTENSION
import com.gcgenome.rms.data.Extension
import com.gcgenome.rms.data.SampleExtension
import org.jooq.DSLContext
import org.jooq.impl.DSL
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.util.*

interface SampleExtensionDao{
    fun DSLContext.deleteSampleExtensionBySampleId(sampleId: UUID): Mono<SampleExtension> {
        return Mono.from(
            deleteFrom(SAMPLE_EXTENSION)
                .where(SAMPLE_EXTENSION.SAMPLE_ID.eq(sampleId))
                .returning()
        ).map { it.into(SampleExtension::class.java) }
    }
}