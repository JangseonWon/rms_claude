package com.gcgenome.rms.dao

import com.gcgenome.rms.data.SampleType
import com.gcgenome.rms.data.Service
import com.gcgenome.rms.tables.records.ServiceRecord
import com.gcgenome.rms.tables.references.SAMPLE_TYPE
import com.gcgenome.rms.tables.references.SERVICE
import com.gcgenome.rms.tables.references.SERVICE_SAMPLE_TYPE
import com.gcgenome.rms.tables.references.USER_SERVICE
import org.jooq.DSLContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface SampleTypeDao  {
    fun DSLContext.selectSampleTypeByServicId(serviceId: String): Flux<SampleType> {
        return Flux.from(
            select(
                SAMPLE_TYPE.ID,
                SAMPLE_TYPE.NAME
            ).from(SAMPLE_TYPE)
                .join(SERVICE_SAMPLE_TYPE).on(SAMPLE_TYPE.ID.eq(SERVICE_SAMPLE_TYPE.SAMPLE_TYPE_ID))
                .where(SERVICE_SAMPLE_TYPE.SERVICE_ID.eq(serviceId))
        ).map { it.into(SampleType::class.java) }
    }
}