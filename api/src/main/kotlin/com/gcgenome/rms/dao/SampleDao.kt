package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Sample
import com.gcgenome.rms.tables.references.*
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.util.*

interface SampleDao {
    fun DSLContext.selectSampleByBarcode(barcode: String): Mono<Sample> =
        Mono.from(selectFrom(SAMPLE).where(SAMPLE.BARCODE.eq(barcode))).map { it.into(Sample::class.java) }

    fun DSLContext.deleteSampleById(sampleId: UUID): Mono<Sample> {
        return Mono.from(
            deleteFrom(SAMPLE)
                .where(SAMPLE.ID.eq(sampleId)).returning()
        ).map { it.into(Sample::class.java) }
    }

}