package com.gcgenome.rms.dao

import com.gcgenome.rms.data.SampleDTO
import com.gcgenome.rms.data.Status
import com.gcgenome.rms.data.UserDTO
import com.gcgenome.rms.tables.references.REQUEST
import com.gcgenome.rms.tables.references.SAMPLE
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.util.*

interface SampleDao {
    fun DSLContext.insertSample(user: UserDTO, sample: SampleDTO, status: Status): Mono<SampleDTO> {
        val insertQuery = insertInto(SAMPLE)
            .set(SAMPLE.ID, UUID.randomUUID())
            .set(SAMPLE.USER_SAMPLE_ID, sample.userSampleId)
            .set(SAMPLE.QUANTITY, sample.quantity)
            .set(SAMPLE.AGE, sample.age)
            .set(SAMPLE.SAMPLING_ON, sample.samplingOn)
            .set(SAMPLE.RESAMPLE_REASON, sample.resampleReason)
            .set(SAMPLE.CREATE_AT, sample.createAt)
            .set(SAMPLE.SAMPLE_TYPE_ID, sample.sampleType!!.id)
            .set(SAMPLE.PATIENT_SERIAL, sample.patient!!.serial)
            .set(SAMPLE.ORGANIZATION_ID, sample.patient!!.organization!!.id)
            .set(SAMPLE.USER_ID, user.id)
            .set(SAMPLE.BARCODE, sample.barcode)

        return Mono.from(insertQuery.returning())
            .map { it.into(SampleDTO::class.java) }
    }

    fun DSLContext.deleteSampleById(sampleId: UUID): Mono<SampleDTO> {
        return Mono.from(
            deleteFrom(SAMPLE)
                .where(
                    SAMPLE.ID.eq(sampleId)
                    .andNotExists(
                        selectOne()
                            .from(REQUEST)
                            .where(REQUEST.SAMPLE_ID.eq(sampleId))
                    )
                ).returning()
        ).map { it.into(SampleDTO::class.java) }
    }
}