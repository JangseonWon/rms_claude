package com.gcgenome.rms.dao

import com.gcgenome.rms.data.SampleDTO
import com.gcgenome.rms.data.Status
import com.gcgenome.rms.data.UserDTO
import com.gcgenome.rms.tables.references.SAMPLE
import org.jooq.DSLContext
import org.jooq.impl.DSL
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

interface SampleDao{
    fun DSLContext.insertSample(user: UserDTO, sample: SampleDTO, status: Status): Mono<SampleDTO> {
        val ofPattern = DateTimeFormatter.ofPattern("yyyyMMdd")
        val currentDate = LocalDateTime.now().format(ofPattern)
        val barcodePrefix = "$currentDate${user.branchSerial}"

        return Mono.from(
            insertInto(SAMPLE)
                .set(SAMPLE.ID, UUID.randomUUID())
                .set(SAMPLE.USER_SAMPLE_ID, sample.userSampleId)
                .set(SAMPLE.QUANTITY, sample.quantity)
                .set(SAMPLE.AGE, sample.age)
                .set(SAMPLE.SAMPLING_ON, sample.samplingOn)
                .set(SAMPLE.RESAMPLE_REASON, sample.resampleReason)
                .set(SAMPLE.CREATE_AT, status.takeIf { it == Status.UNCONFIRMED_ORDER }?.let { LocalDateTime.now() })
                .set(SAMPLE.SAMPLE_TYPE_ID, sample.sampleType!!.id)
                .set(SAMPLE.PATIENT_SERIAL, sample.patient!!.serial)
                .set(SAMPLE.ORGANIZATION_ID, sample.patient!!.organization!!.id)
                .set(SAMPLE.USER_ID, user.id)
                .apply {
                    if(status == Status.UNCONFIRMED_ORDER){
                        set(SAMPLE.BARCODE, select(DSL.coalesce(DSL.max(SAMPLE.BARCODE.cast(Long::class.java).plus(1).cast(String::class.java)), "${barcodePrefix}5000"))
                            .from(SAMPLE)
                            .where(SAMPLE.BARCODE.like("$barcodePrefix%")))
                    }
                }
                .returning()
        ).map { it.into(SampleDTO::class.java) }
    }
}