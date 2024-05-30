package com.gcgenome.rms.dao

import com.gcgenome.rms.authentication.User
import com.gcgenome.rms.data.Sample
import com.gcgenome.rms.tables.references.SAMPLE
import org.jooq.DSLContext
import org.jooq.impl.DSL
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

interface SampleDao {
    fun DSLContext.insertSample(user: User, sample: Sample, status: String): Mono<Sample> {
        val ofPattern = DateTimeFormatter.ofPattern("yyyyMMdd")
        val currentDate = LocalDateTime.now().format(ofPattern)
        val barcodePrefix = "$currentDate${user.branchSerial}"

        return Mono.from(
                insertInto(SAMPLE)
                    .set(SAMPLE.ID, UUID.randomUUID())
                    .set(SAMPLE.BARCODE,
                        select(DSL.coalesce(DSL.max(SAMPLE.BARCODE.cast(Long::class.java).plus(1).cast(String::class.java)), "${barcodePrefix}5000"))
                        .from(SAMPLE)
                        .where(SAMPLE.BARCODE.like("$barcodePrefix%")))
                    .set(SAMPLE.USER_SAMPLE_ID, sample.userSampleId)
                    .set(SAMPLE.QUANTITY, sample.quantity)
                    .set(SAMPLE.AGE, sample.age)
                    .set(SAMPLE.SAMPLING_ON, sample.samplingOn)
                    .set(SAMPLE.RESAMPLE_REASON, sample.resampleReason)
                    .set(SAMPLE.CREATE_AT, status.takeIf { it != "CART" }?.let { LocalDateTime.now() })
                    .set(SAMPLE.SAMPLE_TYPE_ID, sample.sampleType!!.id)
                    .set(SAMPLE.PATIENT_SERIAL, sample.patient!!.serial)
                    .set(SAMPLE.ORGANIZATION_ID, sample.patient.organization!!.id)
                    .set(SAMPLE.USER_ID, user.id)
                    .returning()
            ).map { it.into(Sample::class.java) }
    }
}