package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.records.SampleRecord
import com.gcgenome.rms.tables.references.SAMPLE
import com.gcgenome.rms.data.Sample
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jooq.impl.DSL.count
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

interface SampleDao {
    fun DSLContext.selectSampleById(sampleId: UUID): Flux<SampleRecord> =
        Flux.from(selectFrom(SAMPLE).where(SAMPLE.ID.eq(sampleId)))

    fun DSLContext.selectSamplePostfix(infix: Short): Mono<Int> {
        val ofPattern = DateTimeFormatter.ofPattern("yyyyMMdd")
        return Mono.from(
            select(DSL.coalesce(DSL.max(SAMPLE.GENOME_BARCODE_POSTFIX), 5000))
                .from(SAMPLE)
                .where(SAMPLE.GENOME_BARCODE_PREFIX.eq(LocalDate.now().format(ofPattern).toInt()).and(SAMPLE.GENOME_BARCODE_INFIX.eq(infix))))
            .mapNotNull { it.component1() }
    }

    fun DSLContext.updateSampleById(sample: Sample): Mono<SampleRecord> =
        Mono.from(
            update(SAMPLE)
                .set(SAMPLE.REGISTRATION_AT, LocalDateTime.now())
                .set(SAMPLE.GENOME_BARCODE, sample.genomeBarcode)
                .set(SAMPLE.SAMPLE_BARCODE, sample.sampleBarcode)
                .set(SAMPLE.SAMPLE_TYPE_ID, sample.typeId)
                .set(SAMPLE.AGE, sample.age)
                .set(SAMPLE.SAMPLING, sample.sampling?.atStartOfDay())
                .set(SAMPLE.NOTE, sample.note)
                .set(SAMPLE.DEPARTMENT, sample.department)
                .set(SAMPLE.WARD, sample.ward)
                .set(SAMPLE.PHYSICIAN, sample.physician)
                .where(SAMPLE.ID.eq(sample.id))
                .returning()
        )
    fun DSLContext.insertSample(patientSerial: String, userId: String, itemId: UUID, sample: Sample, organizationId: String?, code: Short, postfix: Int): Mono<SampleRecord> {
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
                .set(SAMPLE.SAMPLE_TYPE_ID, sample.typeId)
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
        )
    }

    fun DSLContext.deleteSample(sampleId: UUID) =
        deleteFrom(SAMPLE).where(SAMPLE.ID.eq(sampleId)).toMono()

    fun DSLContext.findSampleValue(sampleId: UUID): Mono<Pair<String, UUID>> {
        val query = select(SAMPLE.STATE, SAMPLE.ITEM_ID).from(SAMPLE).where(SAMPLE.ID.eq(sampleId))

        return Mono.from(query).map { record ->
            val state = record.getValue(SAMPLE.STATE, String::class.java)
            val itemId = record.getValue(SAMPLE.ITEM_ID, UUID::class.java)
            Pair(state, itemId)
        }
    }

    fun DSLContext.countSampleInItem(itemId: UUID): Mono<Int> =
        Mono.from( select(count(SAMPLE.ITEM_ID).`as`("count")).from(SAMPLE).where(SAMPLE.ITEM_ID.eq(itemId)) )
            .map { r-> r.getValue("count", Int::class.java)}
    fun DSLContext.countSample(itemId: UUID): Mono<Int> =
        Mono.from( select(count(SAMPLE.ITEM_ID).`as`("count")).from(SAMPLE).where(SAMPLE.ITEM_ID.eq(itemId)) )
            .map { r-> r.getValue("count", Int::class.java)}

}