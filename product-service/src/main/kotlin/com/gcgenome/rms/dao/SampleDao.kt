package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Sample
import com.gcgenome.rms.tables.references.SAMPLE
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

interface SampleDao {
    fun DSLContext.selectSampleById(sampleId: UUID): Mono<Sample> =
        Mono.from(selectFrom(SAMPLE).where(SAMPLE.ID.eq(sampleId)))
            .map { it.into(Sample::class.java) }

    fun DSLContext.selectSampleBarcode(barcodePrefix: String): Mono<String?> {
        return Mono.from(select(SAMPLE.BARCODE)
            .from(SAMPLE)
            .where(SAMPLE.BARCODE.like("$barcodePrefix%"))
            .orderBy(SAMPLE.BARCODE.desc())
            .limit(1))
            .mapNotNull { result ->
                result?.component1()
            }
    }


    fun DSLContext.insertSample(sample: Sample, createAt: LocalDateTime?): Mono<Sample> {
        return Mono.from(
            insertInto(SAMPLE)
                .set(SAMPLE.ID, UUID.randomUUID())
                .set(SAMPLE.BARCODE, sample.barcode)
                .set(SAMPLE.USER_SAMPLE_ID, sample.userSampleId)
                .set(SAMPLE.QUANTITY, sample.quantity)
                .set(SAMPLE.AGE, sample.age)
                .set(SAMPLE.SAMPLING_ON, sample.samplingOn)
                .set(SAMPLE.RESAMPLE_REASON, sample.resampleReason)
                .set(SAMPLE.CREATE_AT, createAt)
                .set(SAMPLE.SAMPLE_TYPE_ID, sample.sampleTypeId)
                .set(SAMPLE.PATIENT_SERIAL, sample.patientSerial)
                .set(SAMPLE.ORGANIZATION_ID, sample.organizationId)
                .set(SAMPLE.USER_ID, sample.userId)
                .returning()
        ).map { it.into(Sample::class.java) }
    }
}