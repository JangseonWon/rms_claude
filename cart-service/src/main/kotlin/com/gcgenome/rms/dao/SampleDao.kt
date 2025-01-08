package com.gcgenome.rms.dao

import com.gcgenome.rms.data.SampleDTO
import com.gcgenome.rms.tables.references.REQUEST
import com.gcgenome.rms.tables.references.SAMPLE
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

interface SampleDao {
    fun DSLContext.updateSample(sample: SampleDTO): Mono<SampleDTO> {
        return Mono.from(
            update(SAMPLE)
                .set(SAMPLE.USER_SAMPLE_ID, coalesce(`val`(sample.userSampleId), SAMPLE.USER_SAMPLE_ID))
                .set(SAMPLE.QUANTITY, coalesce(`val`(sample.quantity), SAMPLE.QUANTITY))
                .set(SAMPLE.AGE, coalesce(`val`(sample.age), SAMPLE.AGE))
                .set(SAMPLE.SAMPLING_ON, coalesce(`val`(sample.samplingOn), SAMPLE.SAMPLING_ON))
                .set(SAMPLE.SAMPLE_TYPE_ID, coalesce(`val`(sample.sampleType.id), SAMPLE.SAMPLE_TYPE_ID))
                .set(SAMPLE.PATIENT_SERIAL, coalesce(`val`(sample.patient!!.serial), SAMPLE.PATIENT_SERIAL))
                .set(SAMPLE.ORGANIZATION_ID, coalesce(`val`(sample.patient!!.organization!!.id), SAMPLE.ORGANIZATION_ID))
                .where(SAMPLE.ID.eq(sample.id))
                .returning()
        ).map { it.into(SampleDTO::class.java) }
    }
    fun DSLContext.deleteSampleById(sampleId: UUID): Mono<SampleDTO> {
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
        ).map { it.into(SampleDTO::class.java) }
    }
    fun DSLContext.updateSampleBarcodeAndCreateAtById(sampleId: UUID, branchSerial: String): Mono<SampleDTO> {
        val ofPattern = DateTimeFormatter.ofPattern("yyyyMMdd")
        val currentDate = LocalDateTime.now().format(ofPattern)
        val serialPrefix = currentDate + branchSerial
        val defaultSerial = serialPrefix + "0001"

        return Mono.from(
            update(SAMPLE)
                .set(SAMPLE.CREATE_AT, LocalDateTime.now())
                .set(SAMPLE.BARCODE,
                    select(coalesce(
                        max(SAMPLE.BARCODE).cast(Long::class.java).plus(1).cast(String::class.java)
                        , defaultSerial
                    )).from(SAMPLE).where(SAMPLE.BARCODE.like("$serialPrefix%"))
                )
                .where(SAMPLE.ID.eq(sampleId))
                .returning()
        ).map { it.into(SampleDTO::class.java) }
    }
}