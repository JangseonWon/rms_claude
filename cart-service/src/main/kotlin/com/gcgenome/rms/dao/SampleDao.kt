package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Sample
import com.gcgenome.rms.tables.references.REQUEST
import com.gcgenome.rms.tables.references.SAMPLE
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jooq.impl.DSL.*
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

interface SampleDao {
    fun DSLContext.updateSample(sample: Sample): Mono<Sample> {
        return Mono.from(
            update(SAMPLE)
                .set(SAMPLE.USER_SAMPLE_ID, DSL.coalesce(DSL.`val`(sample.userSampleId), SAMPLE.USER_SAMPLE_ID))
                .set(SAMPLE.QUANTITY, DSL.coalesce(DSL.`val`(sample.quantity), SAMPLE.QUANTITY))
                .set(SAMPLE.AGE, DSL.coalesce(DSL.`val`(sample.age), SAMPLE.AGE))
                .set(SAMPLE.SAMPLING_ON, DSL.coalesce(DSL.`val`(sample.samplingOn), SAMPLE.SAMPLING_ON))
                .set(SAMPLE.SAMPLE_TYPE_ID, DSL.coalesce(DSL.`val`(sample.sampleType!!.id), SAMPLE.SAMPLE_TYPE_ID))
                .set(SAMPLE.PATIENT_SERIAL, DSL.coalesce(DSL.`val`(sample.patient!!.serial), SAMPLE.PATIENT_SERIAL))
                .set(SAMPLE.ORGANIZATION_ID, DSL.coalesce(DSL.`val`(sample.patient!!.organization!!.id), SAMPLE.ORGANIZATION_ID))
                .where(SAMPLE.ID.eq(sample.id))
                .returning()
        ).map { it.into(Sample::class.java) }
    }
    fun DSLContext.deleteSampleById(sampleId: UUID): Mono<Sample> {
        return Mono.from(
            deleteFrom(SAMPLE)
                .where(
                    SAMPLE.ID.eq(sampleId)
                    .andNotExists(
                        selectOne()
                            .from(REQUEST)
                            .where(REQUEST.SAMPLE_ID.eq(sampleId))
                    )
                ).returning()
        ).map { it.into(Sample::class.java) }
    }
    fun DSLContext.selectSampleMaxBarcodeById(sampleId: UUID, branchSerial: String): Mono<String> {
        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val baseSerial = "$currentDate$branchSerial"
        val likePattern = "$baseSerial%"
        val defaultSerial = baseSerial + "5001"

        return Mono.from(
            select(coalesce(
                (max(SAMPLE.BARCODE).cast(Long::class.java).plus(1)).cast(String::class.java), inline(defaultSerial)
            )).from(SAMPLE).where(SAMPLE.BARCODE.like(likePattern))
        ).map { it.into(String::class.java) }
    }
    fun DSLContext.updateSampleBarcodeAndCreateAtById(sampleId: UUID, barcode: String): Mono<Sample> {
        return Mono.from(
            update(SAMPLE)
                .set(SAMPLE.BARCODE, barcode)
                .set(SAMPLE.CREATE_AT, LocalDateTime.now())
                .where(SAMPLE.ID.eq(sampleId))
                .returning()
        ).map { it.into(Sample::class.java) }
    }
}