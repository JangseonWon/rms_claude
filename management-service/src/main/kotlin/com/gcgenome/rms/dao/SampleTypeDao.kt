package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.pojos.SampleType
import com.gcgenome.rms.tables.references.SAMPLE_TYPE
import com.gcgenome.rms.tables.references.SERVICE_SAMPLE_TYPE
import org.jooq.DSLContext
import org.jooq.impl.DSL.lower
import reactor.core.publisher.Flux
import java.util.*


interface SampleTypeDao {
    fun DSLContext.selectSampleTypeByNameOrId(serviceId: String, name: String?): Flux<SampleType> {
        return Flux.from(
            select(SAMPLE_TYPE.ID, SAMPLE_TYPE.NAME)
                .from(SAMPLE_TYPE)
                .join(SERVICE_SAMPLE_TYPE).on(SAMPLE_TYPE.ID.eq(SERVICE_SAMPLE_TYPE.SAMPLE_TYPE_ID))
                .where(SERVICE_SAMPLE_TYPE.SERVICE_ID.eq(serviceId))
                .apply { name?.let { and(lower(SAMPLE_TYPE.NAME).like("%${it.lowercase(Locale.getDefault())}%")) } }
        ).map { it.into(SampleType::class.java) }
    }
}