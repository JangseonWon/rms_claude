package com.gcgenome.rms.order

import com.gcgenome.lims.tables.records.SampleRecord
import com.gcgenome.lims.tables.references.SAMPLE
import com.gcgenome.rms.data.Sample_
import org.jooq.DSLContext
import org.jooq.Record1
import org.jooq.impl.DSL.count
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDateTime
import java.util.*

interface SampleDao {
    fun DSLContext.selectSampleById(sampleId: UUID): Flux<SampleRecord> =
        Flux.from(selectFrom(SAMPLE).where(SAMPLE.ID.eq(sampleId)))
    fun DSLContext.updateSampleById(sample: Sample_): Mono<SampleRecord> =
        Mono.from(
            update(SAMPLE)
                .set(SAMPLE.SERIAL, sample.serial)
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
    fun DSLContext.insertSample(patientSerial: String, userId: String, itemId: UUID, sample: Sample_): Mono<SampleRecord> =
        Mono.from(
            insertInto(SAMPLE)
            .columns(
                SAMPLE.ID,
                SAMPLE.ORGANIZATION_ID,
                SAMPLE.PATIENT_SERIAL,
                SAMPLE.USER_ID,
                SAMPLE.SAMPLE_TYPE_ID,
                SAMPLE.AGE,
                SAMPLE.DEPARTMENT,
                SAMPLE.NOTE,
                SAMPLE.REGISTRATION_AT,
                SAMPLE.SAMPLING,
                SAMPLE.SERIAL,
                SAMPLE.STATE,
                SAMPLE.WARD,
                SAMPLE.PHYSICIAN,
                SAMPLE.ITEM_ID)
            .values(
                UUID.randomUUID(),
                userId,
                patientSerial,
                userId,
                sample.typeId,
                sample.age,
                sample.department,
                sample.note,
                LocalDateTime.now(),
                sample.sampling!!.atStartOfDay(),
                sample.serial,
                "REQUEST",
                sample.ward,
                sample.physician,
                itemId
            ).returning()
        )

    fun DSLContext.deleteSample(sampleId: UUID): Mono<SampleRecord> =
        deleteFrom(SAMPLE).where(SAMPLE.ID.eq(sampleId))
            .returning()
            .toMono()

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
