package com.gcgenome.rms.dao

import com.gcgenome.rms.data.ExtensionDTO
import com.gcgenome.rms.data.SampleExtensionDTO
import com.gcgenome.rms.tables.references.SAMPLE_EXTENSION
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.util.*

interface SampleExtensionDao{
    fun DSLContext.insertSampleExtension(sampleExtension: SampleExtensionDTO): Mono<ExtensionDTO> {
        return Mono.from(
            insertInto(SAMPLE_EXTENSION)
                .set(SAMPLE_EXTENSION.SAMPLE_ID, sampleExtension.sampleId)
                .set(SAMPLE_EXTENSION.EXTENSION_ID, sampleExtension.extensionId)
                .set(SAMPLE_EXTENSION.VALUE, sampleExtension.value)
                .returning()
        ).map { it.into(ExtensionDTO::class.java) }
    }
}