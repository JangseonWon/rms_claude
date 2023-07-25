package com.gcgenome.rms.service

import com.gcgenome.rms.tables.references.*
import com.gcgenome.rms.data.Service
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import reactor.core.publisher.Flux

interface Dao {
    fun DSLContext.selectUserService(userId: String): Flux<Service> {
        val query = select(
            SERVICE.ID,
            SERVICE.NAME,
            jsonArrayAgg(jsonObject(
                key("id").value(SAMPLE_TYPE.ID),
                key("name").value(SAMPLE_TYPE.NAME)
            )).`as`("sampleTypes"),
            `when`(count(SERVICE_EXTENSION.EXTENSION_ID).greaterThan(0),
                jsonArrayAgg(jsonObject(
                    key("id").value(EXTENSION.ID),
                    key("name").value(EXTENSION.NAME),
                    key("required").value(SERVICE_EXTENSION.REQUIRED),
                    key("regex").value(EXTENSION.REGEX)
                ))
            ).`as`("extensions"),
        ).from(USER_SERVICE)
            .join(SERVICE).on(USER_SERVICE.SERVICE_ID.eq(SERVICE.ID))
            .leftOuterJoin(SERVICE_EXTENSION).on(SERVICE.ID.eq(SERVICE_EXTENSION.SERVICE_ID))
            .leftOuterJoin(EXTENSION).on(SERVICE_EXTENSION.EXTENSION_ID.eq(EXTENSION.ID))
            .join(SERVICE_SAMPLE_TYPE).on(SERVICE.ID.eq(SERVICE_SAMPLE_TYPE.SERVICE_ID))
            .join(SAMPLE_TYPE).on(SERVICE_SAMPLE_TYPE.SAMPLE_TYPE_ID.eq(SAMPLE_TYPE.ID))
            .where(USER_SERVICE.USER_ID.eq(userId))
            .groupBy(SERVICE.ID)
        return Flux.from(query).map(Service::toModel)
    }
}
