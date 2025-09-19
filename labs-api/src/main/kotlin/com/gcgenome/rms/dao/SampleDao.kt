package com.gcgenome.rms.dao

import com.gcgenome.rms.entity.SampleEntity
import com.gcgenome.rms.request.dto.request.SamplePatchDTO
import com.gcgenome.rms.request.dto.response.RequestSampleDTO
import com.gcgenome.rms.tables.references.REQUEST
import com.gcgenome.rms.tables.references.SAMPLE
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Records
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

interface SampleDao {
    fun DSLContext.insertSample(userId: UUID, sample: SampleEntity): Mono<RequestSampleDTO> {
        return Mono.from(
            insertInto(SAMPLE)
                .set(SAMPLE.ID, sample.id)
                .set(SAMPLE.SERIAL, sample.serial)
                .set(SAMPLE.COUNT, sample.count)
                .set(SAMPLE.SAMPLING_ON, sample.samplingOn)
                .set(SAMPLE.CREATE_AT, LocalDateTime.now())
                .set(SAMPLE.SAMPLE_TYPE_ID, sample.sampleTypeId)
                .set(SAMPLE.USER_ID, userId)
                .returning()
        ).map { it.into(RequestSampleDTO::class.java) }
    }
    /*
        fun DSLContext.selectSampleByUserIdAndSerial(userId: UUID, serial: String): Mono<RequestSampleDTO> {
            return Mono.from(
                selectFrom(SAMPLE)
                    .where(SAMPLE.USER_ID.eq(userId).and(SAMPLE.SERIAL.eq(serial)))
            ).map { it.into(RequestSampleDTO::class.java) }
        }
    */
    fun DSLContext.selectSampleByUserIdAndSerial(
        userId: UUID,
        serial: String
    ): Mono<RequestSampleDTO> {
        val mapper = Records.mapping { id: UUID,
                                       barcode: String?,
                                       serialStr: String,
                                       count: Int,
                                       samplingOn: LocalDate,
                                       sampleTypeId: UUID,
                                       userIdVal: UUID ->
            RequestSampleDTO(
                id = id,
                barcode = barcode,
                serial = serialStr,
                count = count,
                samplingOn = samplingOn,
                sampleTypeId = sampleTypeId,
                userId = userIdVal,
                type = null
            )
        }

        val q = select(
            SAMPLE.ID,
            SAMPLE.BARCODE,
            SAMPLE.SERIAL,
            SAMPLE.COUNT.cast(Int::class.javaObjectType),
            SAMPLE.SAMPLING_ON.cast(LocalDate::class.java),
            SAMPLE.SAMPLE_TYPE_ID,
            SAMPLE.USER_ID
        )
            .from(SAMPLE)
            .where(SAMPLE.USER_ID.eq(userId).and(SAMPLE.SERIAL.eq(serial)))

        return Mono.from(q).map { rec -> mapper.map(rec) }
    }


    fun DSLContext.deleteSampleById(id: UUID): Mono<RequestSampleDTO> {
        return Mono.from(
            deleteFrom(SAMPLE)
                .where(SAMPLE.ID.eq(id))
                .andNotExists(
                    selectOne()
                        .from(REQUEST)
                        .where(REQUEST.SAMPLE_ID.eq(id))
                )
                .returning()
        ).map { it.into(RequestSampleDTO::class.java) }
    }
    fun DSLContext.updateSampleById(sampleId: UUID, patch: SamplePatchDTO): Mono<Boolean> {
        val updates = mutableMapOf<Field<*>, Any?>()
        if (patch.count.isPresent)      updates[SAMPLE.COUNT]       = patch.count.orElse(null)
        if (patch.samplingOn.isPresent) updates[SAMPLE.SAMPLING_ON] = patch.samplingOn.orElse(null)?.let(LocalDate::parse)

        if (updates.isEmpty()) return Mono.just(false)
        return Mono.from(update(SAMPLE).set(updates).where(SAMPLE.ID.eq(sampleId))).thenReturn(true)
    }


}