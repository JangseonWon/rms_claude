package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Sample
import com.gcgenome.rms.tables.references.*
import org.jooq.Condition
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

interface SampleDao {

    fun DSLContext.insertSample(userId:String, sample: Sample,barcode: String): Mono<Sample>{
        return Mono.from(
            insertInto(SAMPLE)
                .set(SAMPLE.ID, UUID.randomUUID())
                .set(SAMPLE.BARCODE,barcode)
                .set(SAMPLE.USER_SAMPLE_ID,sample.userSampleId)
                .set(SAMPLE.QUANTITY,sample.quantity)
                .set(SAMPLE.AGE,sample.age)
                .set(SAMPLE.SAMPLING_ON,sample.samplingOn)
                .set(SAMPLE.CREATE_AT, LocalDateTime.now())
                .set(SAMPLE.SAMPLE_TYPE_ID,sample.sampleType!!.id)
                .set(SAMPLE.PATIENT_SERIAL,sample.patient!!.serial)
                .set(SAMPLE.ORGANIZATION_ID,sample.patient.organization!!.id)
                .set(SAMPLE.USER_ID,userId)
                .onConflictDoNothing()
                .returning()
        ).map { it.into(Sample::class.java) }
    }

    fun DSLContext.selectSampleByCondition(whereClause: Condition):Mono<Sample> {
        return Mono.from(
            selectFrom(SAMPLE)
                .where(whereClause)
        ).map { it.into(Sample::class.java) }.switchIfEmpty(Mono.empty())
    }

    fun DSLContext.selectSampleBarcode(barcodePrefix: String): Mono<String> {
        return Mono.from(
            select(SAMPLE.BARCODE)
                .from(SAMPLE)
                .where(SAMPLE.BARCODE.like("$barcodePrefix%"))
                .orderBy(SAMPLE.BARCODE.desc())
                .limit(1)
        ).map { it.into(String::class.java) }
    }

    fun DSLContext.selectSampleByBarcode(barcode: String): Mono<Sample> =
        Mono.from(selectFrom(SAMPLE).where(SAMPLE.BARCODE.eq(barcode))).map { it.into(Sample::class.java) }

    fun DSLContext.deleteSampleById(sampleId: UUID): Mono<Sample> {
        return Mono.from(
            deleteFrom(SAMPLE)
                .where(SAMPLE.ID.eq(sampleId)).returning()
        ).map { it.into(Sample::class.java) }
    }

}