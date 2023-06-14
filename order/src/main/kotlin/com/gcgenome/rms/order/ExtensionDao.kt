package com.gcgenome.rms.order

import com.gcgenome.lims.tables.references.SAMPLE_EXTENSION
import com.gcgenome.rms.data.Extension_
import org.jooq.Configuration
import org.springframework.stereotype.Repository
import java.util.*

@Repository("com.gcgenome.rms.order.ExtensionDao")
class ExtensionDao{
    fun insertSampleExtension(trx: Configuration, sampleExtension: Extension_, sampleId: UUID) = trx.dsl()
        .insertInto(SAMPLE_EXTENSION)
        .columns(SAMPLE_EXTENSION.EXTENSION_ID, SAMPLE_EXTENSION.SAMPLE_ID, SAMPLE_EXTENSION.VALUE)
        .values(sampleExtension.id, sampleId, sampleExtension.value)
        .returning()
}