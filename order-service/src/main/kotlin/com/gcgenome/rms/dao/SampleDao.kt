package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Sample
import com.gcgenome.rms.tables.references.SAMPLE
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
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

    fun DSLContext.selectSampleUserIdByBarcode(barcodePrefix: String): Mono<String> {
        return Mono.from(select(SAMPLE.USER_ID)
            .from(SAMPLE)
            .where(SAMPLE.BARCODE.like("$barcodePrefix%"))
            .orderBy(SAMPLE.BARCODE.desc())
            .limit(1))
            .mapNotNull { result ->
                result?.component1()
            }
    }

    fun DSLContext.deleteSampleById(sampleId: UUID): Mono<Sample> {
        return Mono.from(
            deleteFrom(SAMPLE).where(SAMPLE.ID.eq(sampleId))
                .returning()
        ).map { it.into(Sample::class.java) }
    }

    fun DSLContext.insertSample(barcode: String, sample: Sample): Mono<Sample> {
        return Mono.from(
            insertInto(SAMPLE)
                .set(SAMPLE.ID, UUID.randomUUID())
                .set(SAMPLE.BARCODE, barcode)
                .set(SAMPLE.USER_SAMPLE_ID, sample.userSampleId ?: barcode)
                .set(SAMPLE.QUANTITY, sample.quantity)
                .set(SAMPLE.AGE, sample.age)
                .set(SAMPLE.SAMPLING_ON, sample.samplingOn)
                .set(SAMPLE.RESAMPLE_REASON, sample.resampleReason)
                .set(SAMPLE.CREATE_AT, LocalDateTime.now())
                .set(SAMPLE.SAMPLE_TYPE_ID, sample.sampleType.id)
                .set(SAMPLE.PATIENT_SERIAL, sample.patientSerial)
                .set(SAMPLE.ORGANIZATION_ID, sample.patient!!.organization!!.id)
                .set(SAMPLE.USER_ID, sample.userId)
                .returning()
        ).map { it.into(Sample::class.java) }
    }

    fun DSLContext.updateSample(sample: Sample, organizationId : String): Mono<Sample> {
        return Mono.from(
            update(SAMPLE)
                .set(SAMPLE.PATIENT_SERIAL, coalesce(`val`(sample.patientSerial), SAMPLE.PATIENT_SERIAL))
                .set(SAMPLE.BARCODE, coalesce(`val`(sample.barcode), SAMPLE.BARCODE))
                .set(SAMPLE.USER_SAMPLE_ID, coalesce(`val`(sample.userSampleId ?: sample.barcode), SAMPLE.USER_SAMPLE_ID))
                .set(SAMPLE.QUANTITY, coalesce(`val`(sample.quantity), SAMPLE.QUANTITY))
                .set(SAMPLE.AGE, coalesce(`val`(sample.age), SAMPLE.AGE))
                .set(SAMPLE.SAMPLING_ON, coalesce(`val`(sample.samplingOn), SAMPLE.SAMPLING_ON))
                .set(SAMPLE.RESAMPLE_REASON, coalesce(`val`(sample.resampleReason), SAMPLE.RESAMPLE_REASON))
                .set(SAMPLE.SAMPLE_TYPE_ID, coalesce(`val`(sample.sampleType.id), SAMPLE.SAMPLE_TYPE_ID))
                .set(SAMPLE.ORGANIZATION_ID, coalesce(`val`(organizationId), SAMPLE.ORGANIZATION_ID))
                .set(SAMPLE.USER_ID, coalesce(`val`(sample.userId), SAMPLE.USER_ID))
                .where(SAMPLE.ID.eq(sample.id))
                .returning()
        ).map { it.into(Sample::class.java) }
    }
}