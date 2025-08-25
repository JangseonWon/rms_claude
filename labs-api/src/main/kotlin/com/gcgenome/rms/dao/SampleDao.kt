package com.gcgenome.rms.dao

import com.gcgenome.rms.data.SampleDTO
import com.gcgenome.rms.data.patch.SamplePatchDTO
import com.gcgenome.rms.tables.references.*
import org.jooq.DSLContext
import org.jooq.Field
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

interface SampleDao {
    fun DSLContext.insertSample(userId: UUID, sample: SampleDTO): Mono<SampleDTO> {
        return Mono.from(
            insertInto(SAMPLE)
                .set(SAMPLE.ID, sample.id)
                .set(SAMPLE.SERIAL, sample.serial)
                .set(SAMPLE.COUNT, sample.count)
                .set(SAMPLE.AGE, sample.age)
                .set(SAMPLE.SAMPLING_ON, sample.samplingOn)
                .set(SAMPLE.CREATE_AT, LocalDateTime.now())
                .set(SAMPLE.SAMPLE_TYPE_ID, sample.sampleTypeId)
                .set(SAMPLE.USER_ID, userId)
                .returning()
        ).map { it.into(SampleDTO::class.java) }
    }

    fun DSLContext.selectSampleByUserIdAndSerial(userId: UUID, serial: String): Mono<SampleDTO> {
        return Mono.from(
            selectFrom(SAMPLE)
                .where(SAMPLE.USER_ID.eq(userId).and(SAMPLE.SERIAL.eq(serial)))
        ).map { it.into(SampleDTO::class.java) }
    }
    fun DSLContext.insertOrGetSample(userId: UUID, sample: SampleDTO): Mono<SampleDTO> {
        return Mono.from(
            insertInto(SAMPLE)
                .set(SAMPLE.ID, sample.id)
                .set(SAMPLE.SERIAL, sample.serial)
                .set(SAMPLE.COUNT, sample.count)
                .set(SAMPLE.AGE, sample.age)
                .set(SAMPLE.SAMPLING_ON, sample.samplingOn)
                .set(SAMPLE.CREATE_AT, LocalDateTime.now())
                .set(SAMPLE.SAMPLE_TYPE_ID, sample.sampleTypeId)
                .set(SAMPLE.USER_ID, userId)
                .onConflict(SAMPLE.USER_ID, SAMPLE.SERIAL)
                .doNothing()
                .returning()
        )
            .map { it.into(SampleDTO::class.java) }
            .switchIfEmpty(selectSampleByUserIdAndSerial(userId, sample.serial!!))
    }

    fun DSLContext.deleteSampleById(id: UUID): Mono<SampleDTO> {
        return Mono.from(
            deleteFrom(SAMPLE)
                .where(SAMPLE.ID.eq(id))
                .andNotExists(
                    selectOne()
                        .from(REQUEST)
                        .where(REQUEST.SAMPLE_ID.eq(id))
                )
                .returning()
        ).map { it.into(SampleDTO::class.java) }
    }
    fun DSLContext.updateSampleById(sampleId: UUID, patch: SamplePatchDTO): Mono<Boolean> {
        val updates = mutableMapOf<Field<*>, Any?>()
        if (patch.count.isPresent)      updates[SAMPLE.COUNT]       = patch.count.orElse(null)
        if (patch.age.isPresent)        updates[SAMPLE.AGE]         = patch.age.orElse(null)
        if (patch.samplingOn.isPresent) updates[SAMPLE.SAMPLING_ON] = patch.samplingOn.orElse(null)?.let(LocalDate::parse)

        if (updates.isEmpty()) return Mono.just(false)
        return Mono.from(update(SAMPLE).set(updates).where(SAMPLE.ID.eq(sampleId))).thenReturn(true)
    }


}