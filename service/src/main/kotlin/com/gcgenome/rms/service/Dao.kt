package com.gcgenome.rms.service

import com.gcgenome.rms.tables.references.*
import com.gcgenome.rms.data.Extension_
import com.gcgenome.rms.data.SampleType_
import com.gcgenome.rms.data.Service_
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository("com.gcgenome.rms.service.Dao")
class Dao(val dslContext: DSLContext) {
    fun findByUserService(userId: String): Flux<Service_> {
        val query = dslContext.select(
            SERVICE.ID,
            SERVICE.NAME,
            jsonArrayAgg(jsonObject(
                key("id").value(EXTENSION.ID),
                key("name").value(EXTENSION.NAME),
                key("required").value(SERVICE_EXTENSION.REQUIRED),
                key("regex").value(EXTENSION.REGEX)
            )).`as`("extensions"),
            jsonArrayAgg(jsonObject(
                key("id").value(SAMPLE_TYPE.ID),
                key("name").value(SAMPLE_TYPE.NAME)
            )).`as`("sample_types")
            )
            .from(ORGANIZATION_SERVICE)
            .join(SERVICE).on(ORGANIZATION_SERVICE.SERVICE_ID.eq(SERVICE.ID))
            .join(SERVICE_EXTENSION).on(SERVICE.ID.eq(SERVICE_EXTENSION.SERVICE_ID))
            .join(EXTENSION).on(SERVICE_EXTENSION.EXTENSION_ID.eq(EXTENSION.ID))
            .join(SERVICE_SAMPLE_TYPE).on(SERVICE.ID.eq(SERVICE_SAMPLE_TYPE.SERVICE_ID))
            .join(SAMPLE_TYPE).on(SERVICE_SAMPLE_TYPE.SAMPLE_TYPE_ID.eq(SAMPLE_TYPE.ID))
            .where(ORGANIZATION_SERVICE.ORGANIZATION_ID.eq(userId))
            .groupBy(SERVICE.ID)
        return Flux.from(query).map{record->
            Service_(
                id = record.getValue("id", String::class.java),
                name = record.getValue("name", String::class.java),
                extensions = record.getValue("extensions", Array<Extension_>::class.java).toList(),
                sampleTypes = record.getValue("sample_types", Array<SampleType_>::class.java).toList()
            )
        }
    }

}
