package com.gcgenome.rms.dao

import com.gcgenome.rms.data.ExtensionDTO
import com.gcgenome.rms.tables.records.SampleExtensionRecord
import com.gcgenome.rms.tables.references.SAMPLE_EXTENSION
import org.jooq.DSLContext
import reactor.core.publisher.Flux
import java.util.*

interface SampleExtensionDao {
    fun DSLContext.insertSampleExtension(sampleExtension: ExtensionDTO, sampleId: UUID): Flux<SampleExtensionRecord> {
        return Flux.from(
            insertInto(SAMPLE_EXTENSION)
                .set(SAMPLE_EXTENSION.EXTENSION_ID, sampleExtension.id)
                .set(SAMPLE_EXTENSION.SAMPLE_ID, sampleId)
                .set(SAMPLE_EXTENSION.VALUE, sampleExtension.value)
                .returning()
        )
    }
}