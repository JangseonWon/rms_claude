package com.gcgenome.lims.dao

import com.gcgenome.lims.data.Sample
import com.gcgenome.rms.tables.references.SAMPLE
import org.jooq.DSLContext
import reactor.core.publisher.Mono

interface SampleDao {
    fun DSLContext.selectSampleByBarcode(barcode: String): Mono<Sample> {
        return Mono.from(
            select(SAMPLE).from(SAMPLE).where(SAMPLE.BARCODE.eq(barcode))
        ).map { it.into(Sample::class.java) }
    }
}