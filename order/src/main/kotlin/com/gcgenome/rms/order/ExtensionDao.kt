package com.gcgenome.rms.order

import com.gcgenome.lims.tables.references.SAMPLE_EXTENSION
import com.gcgenome.rms.data.Extension_
import org.jooq.DSLContext
import java.util.*

interface ExtensionDao{
    fun DSLContext.insertSampleExtension(sampleExtension: Extension_, sampleId: UUID) =
        insertInto(SAMPLE_EXTENSION)
        .columns(SAMPLE_EXTENSION.EXTENSION_ID, SAMPLE_EXTENSION.SAMPLE_ID, SAMPLE_EXTENSION.VALUE)
        .values(sampleExtension.id, sampleId, sampleExtension.value)
        .returning()
}