package com.gcgenome.rms.order

import com.gcgenome.lims.tables.references.SAMPLE
import com.gcgenome.rms.data.Sample
import org.jooq.DSLContext
import org.jooq.impl.DSL.count
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDateTime
import java.util.*

interface SampleDao {
    fun DSLContext.selectSampleById(sampleId: UUID) =
        select(SAMPLE).from(SAMPLE).where(SAMPLE.ID.eq(sampleId))
    fun DSLContext.updateSampleById(sample: Sample) =
        Mono.from(
            update(SAMPLE)
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
    fun DSLContext.insertSample(patientSerial: String, userId: String, itemId: UUID, sample: Sample, organizationId: String?) =
        Mono.from(
            insertInto(SAMPLE)
            .columns(
                SAMPLE.ID,
                SAMPLE.CREATE_AT,
                SAMPLE.LAST_MODIFY_AT,
                SAMPLE.GENOME_BARCODE,
                SAMPLE.SAMPLE_BARCODE,
                SAMPLE.ORGANIZATION_ID,
                SAMPLE.PATIENT_SERIAL,
                SAMPLE.USER_ID,
                SAMPLE.SAMPLE_TYPE_ID,
                SAMPLE.AGE,
                SAMPLE.DEPARTMENT,
                SAMPLE.NOTE,
                SAMPLE.REGISTRATION_AT,
                SAMPLE.SAMPLING,
                SAMPLE.STATE,
                SAMPLE.WARD,
                SAMPLE.PHYSICIAN,
                SAMPLE.ITEM_ID)
            .values(
                UUID.randomUUID(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                sample.genomeBarcode,
                sample.sampleBarcode,
                organizationId ?: userId,
                patientSerial,
                userId,
                sample.typeId,
                sample.age,
                sample.department,
                sample.note,
                LocalDateTime.now(),
                sample.sampling!!.atStartOfDay(),
                "REQUEST",
                sample.ward,
                sample.physician,
                itemId
            ).returning()
        )

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