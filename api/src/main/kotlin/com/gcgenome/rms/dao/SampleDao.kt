package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.references.SAMPLE
import com.gcgenome.rms.data.Sample
import com.gcgenome.rms.data.SampleState
import com.gcgenome.rms.tables.references.REPORT
import com.gcgenome.rms.tables.references.SAMPLE_EXTENSION
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

interface SampleDao {
    fun DSLContext.selectSampleById(sampleId: UUID): Mono<Sample> =
        Mono.from(selectFrom(SAMPLE).where(SAMPLE.ID.eq(sampleId))).map { it.into(Sample::class.java) }

    fun DSLContext.selectSamplePostfix(infix: Short): Mono<Int> {
        val ofPattern = DateTimeFormatter.ofPattern("yyyyMMdd")
        return Mono.from(
            select(coalesce(max(SAMPLE.GENOME_BARCODE_POSTFIX), 5000))
                .from(SAMPLE)
                .where(SAMPLE.GENOME_BARCODE_PREFIX.eq(LocalDate.now().format(ofPattern).toInt()).and(SAMPLE.GENOME_BARCODE_INFIX.eq(infix))))
            .mapNotNull { it.component1() }
    }
    fun DSLContext.insertSample(patientSerial: String, userId: String, itemId: UUID, sample: Sample, organizationId: String?, code: Short, postfix: Int): Mono<Sample> {
        val ofPattern = DateTimeFormatter.ofPattern("yyyyMMdd")
        return Mono.from(
            insertInto(SAMPLE)
                .set(SAMPLE.ID, UUID.randomUUID())
                .set(SAMPLE.CREATE_AT, LocalDateTime.now())
                .set(SAMPLE.LAST_MODIFY_AT, LocalDateTime.now())
                .set(SAMPLE.GENOME_BARCODE_PREFIX, LocalDate.now().format(ofPattern).toInt())
                .set(SAMPLE.GENOME_BARCODE_INFIX, code)
                .set(SAMPLE.GENOME_BARCODE_POSTFIX, postfix)
                .set(SAMPLE.GENOME_BARCODE, "${LocalDate.now().format(ofPattern).toInt()}${code}${postfix}")
                .set(SAMPLE.SAMPLE_BARCODE, sample.sampleBarcode)
                .set(SAMPLE.ORGANIZATION_ID, organizationId ?: userId)
                .set(SAMPLE.PATIENT_SERIAL, patientSerial)
                .set(SAMPLE.USER_ID, userId)
                .set(SAMPLE.SAMPLE_TYPE_ID, sample.sampleTypeId)
                .set(SAMPLE.AGE, sample.age)
                .set(SAMPLE.DEPARTMENT, sample.department)
                .set(SAMPLE.NOTE, sample.note)
                .set(SAMPLE.REGISTRATION_AT, LocalDateTime.now())
                .set(SAMPLE.SAMPLING, sample.sampling!!.atStartOfDay())
                .set(SAMPLE.STATE, "NEW")
                .set(SAMPLE.WARD, sample.ward)
                .set(SAMPLE.PHYSICIAN, sample.physician)
                .set(SAMPLE.ITEM_ID, itemId)
                .set(SAMPLE.EMP_ID, sample.empId)
                .set(SAMPLE.EMP_NAME, sample.empName)
                .set(SAMPLE.EMP_MOBILE, sample.empMobile)
                .returning()
        ).map { it.into(Sample::class.java) }
    }
    fun DSLContext.selectSampleByCreateAt(userId: String, orderDateFrom: LocalDateTime, orderDateTo: LocalDateTime): Flux<Sample> {
        return Flux.from(
            select(
                SAMPLE.ID,
                SAMPLE.CREATE_AT,
                SAMPLE.LAST_MODIFY_AT,
                SAMPLE.AGE,
                SAMPLE.SAMPLING,
                SAMPLE.NOTE,
                SAMPLE.GENOME_BARCODE,
                SAMPLE.SAMPLE_BARCODE,
                SAMPLE.SAMPLE_TYPE_ID,
                SAMPLE.DEPARTMENT,
                SAMPLE.WARD,
                SAMPLE.PHYSICIAN,
                SAMPLE.STATE,
                jsonArrayAgg(jsonObject(
                    key("id").value(SAMPLE_EXTENSION.EXTENSION_ID),
                    key("value").value(SAMPLE_EXTENSION.VALUE)
                )).`as`("extensions"),
                field(
                    select(
                        jsonArrayAgg(jsonObject(
                            key("id").value(REPORT.ID),
                            key("create_at").value(REPORT.CREATE_AT),
                            key("reported_at").value(REPORT.REPORTED_AT),
                            key("type").value(REPORT.TYPE),
                            key("value").value(REPORT.VALUE)
                        ))
                    ).from(REPORT).where(SAMPLE.ID.eq(REPORT.SAMPLE_ID))
                ).`as`("reports")
            ).from(SAMPLE)
            .join(SAMPLE_EXTENSION).on(SAMPLE.ID.eq(SAMPLE_EXTENSION.SAMPLE_ID))
            .where(SAMPLE.USER_ID.eq(userId)
                .and(SAMPLE.CREATE_AT.between(orderDateFrom, orderDateTo))
                .and(SAMPLE.STATE.eq(SampleState.FINISHED.name))
            ).groupBy(SAMPLE.ID))
            .map { it.into(Sample::class.java) }
    }

    fun DSLContext.deleteSampleById(sampleId: UUID): Mono<Sample> {
        return Mono.from(deleteFrom(SAMPLE).where(SAMPLE.ID.eq(sampleId)).returning()).map { it.into(Sample::class.java) }
    }
    fun DSLContext.updateSampleState(sampleId: UUID, state: SampleState): Mono<Sample> {
        return Mono.from(update(SAMPLE).set(SAMPLE.STATE, state.name).where(SAMPLE.ID.eq(sampleId)).returning()).map { it.into(Sample::class.java) }
    }
}