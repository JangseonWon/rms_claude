package com.gcgenome.rms.dao

import com.gcgenome.rms.data.SampleType
import com.gcgenome.rms.tables.references.SAMPLE_TYPE
import com.gcgenome.rms.tables.references.SERVICE_SAMPLE_TYPE
import org.jooq.DSLContext
import reactor.core.publisher.Flux

interface SampleTypeDao {
    fun DSLContext.selectSampleTypeByServiceId(serviceId: String): Flux<SampleType> {
        return Flux.from(
            select(
                SAMPLE_TYPE.ID,
                SAMPLE_TYPE.NAME
            ).from(SERVICE_SAMPLE_TYPE)
            .join(SAMPLE_TYPE).on(SERVICE_SAMPLE_TYPE.SAMPLE_TYPE_ID.eq(SAMPLE_TYPE.ID))
            .where(SERVICE_SAMPLE_TYPE.SERVICE_ID.eq(serviceId))
        ).map { it.into(SampleType::class.java) }
    }
}