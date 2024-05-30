package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Sample
import com.gcgenome.rms.tables.references.REQUEST
import com.gcgenome.rms.tables.references.SAMPLE
import org.jooq.DSLContext
import org.jooq.impl.DSL
import reactor.core.publisher.Mono
import java.util.*

interface SampleDao {
    fun DSLContext.updateSample(sample: Sample): Mono<Sample> {
        return Mono.from(
            update(SAMPLE)
                .set(SAMPLE.USER_SAMPLE_ID, DSL.coalesce(DSL.`val`(sample.userSampleId), SAMPLE.USER_SAMPLE_ID))
                .set(SAMPLE.QUANTITY, DSL.coalesce(DSL.`val`(sample.quantity), SAMPLE.QUANTITY))
                .set(SAMPLE.AGE, DSL.coalesce(DSL.`val`(sample.age), SAMPLE.AGE))
                .set(SAMPLE.SAMPLING_ON, DSL.coalesce(DSL.`val`(sample.samplingOn), SAMPLE.SAMPLING_ON))
                .set(SAMPLE.SAMPLE_TYPE_ID, DSL.coalesce(DSL.`val`(sample.sampleType!!.id), SAMPLE.SAMPLE_TYPE_ID))
                .set(SAMPLE.PATIENT_SERIAL, DSL.coalesce(DSL.`val`(sample.patient!!.serial), SAMPLE.PATIENT_SERIAL))
                .set(SAMPLE.ORGANIZATION_ID, DSL.coalesce(DSL.`val`(sample.patient!!.organization!!.id), SAMPLE.ORGANIZATION_ID))
                .where(SAMPLE.ID.eq(sample.id))
                .returning()
        ).map { it.into(Sample::class.java) }
    }
    fun DSLContext.deleteSampleById(sampleId: UUID): Mono<Sample> {
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
        ).map { it.into(Sample::class.java) }
    }
}