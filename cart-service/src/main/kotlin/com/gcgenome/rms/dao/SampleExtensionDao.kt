package com.gcgenome.rms.dao

import com.gcgenome.rms.data.ExtensionDTO
import com.gcgenome.rms.tables.references.SAMPLE_EXTENSION
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.util.*

interface SampleExtensionDao {
    fun DSLContext.deleteSampleExtensionBySampleId(sampleId: UUID): Mono<ExtensionDTO> {
        return Mono.from(
            deleteFrom(SAMPLE_EXTENSION).where(SAMPLE_EXTENSION.SAMPLE_ID.eq(sampleId)).returning()
        ).map { it.into(ExtensionDTO::class.java) }
    }

    fun DSLContext.updateSampleExtensionBySampleId(sampleId: UUID, extension: ExtensionDTO): Mono<ExtensionDTO> {
        return Mono.from(
            update(SAMPLE_EXTENSION)
                .set(SAMPLE_EXTENSION.VALUE, extension.value)
                .where(SAMPLE_EXTENSION.SAMPLE_ID.eq(sampleId).and(SAMPLE_EXTENSION.EXTENSION_ID.eq(extension.id)))
                .returning()
        ).map { it.into(ExtensionDTO::class.java) }
    }
}