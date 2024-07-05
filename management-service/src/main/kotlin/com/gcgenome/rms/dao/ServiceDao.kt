package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Service_
import com.gcgenome.rms.tables.pojos.Service
import com.gcgenome.rms.tables.references.*
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jooq.impl.DSL.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


interface ServiceDao{
    fun DSLContext.selectServiceByUserId(userId: String): Flux<Service_>{
        return Flux.from(
            select(
                SERVICE.ID,
                SERVICE.NAME,
                field(
                    select(
                        jsonArrayAgg(
                            jsonObject(
                                key("id").value(EXTENSION.ID),
                                key("name").value(EXTENSION.NAME),
                                key("regex").value(EXTENSION.REGEX),
                                key("required").value(SERVICE_EXTENSION.REQUIRED),
                            )
                        )
                    ).from(EXTENSION)
                    .join(SERVICE_EXTENSION).on(EXTENSION.ID.eq(SERVICE_EXTENSION.EXTENSION_ID))
                        .where(SERVICE_EXTENSION.SERVICE_ID.eq(SERVICE.ID))
                ).`as`("extensions"),
                field(
                    select(
                        jsonArrayAgg(
                            jsonObject(
                                key("id").value(SAMPLE_TYPE.ID),
                                key("name").value(SAMPLE_TYPE.NAME)
                            )
                        )
                    ).from(SAMPLE_TYPE)
                        .join(SERVICE_SAMPLE_TYPE).on(SAMPLE_TYPE.ID.eq(SERVICE_SAMPLE_TYPE.SAMPLE_TYPE_ID))
                        .where(SERVICE.ID.eq(SERVICE_SAMPLE_TYPE.SERVICE_ID))
                ).`as`("sample_types")
            ).from(SERVICE)
                .join(USER_SERVICE).on(SERVICE.ID.eq(USER_SERVICE.SERVICE_ID))
                .where(USER_SERVICE.USER_ID.eq(userId))
        ).map { it.into(Service_::class.java) }
    }

    fun DSLContext.selectServiceById(serviceId: String): Mono<Service_> {
        return Mono.from(
            selectFrom(SERVICE).where(SERVICE.ID.eq(serviceId))
        ).map { it.into(Service_::class.java) }
    }

    fun DSLContext.selectServiceByNameOrId(whereClause: Condition): Flux<Service> {
        return Flux.from(
            selectFrom(SERVICE).where(whereClause)
        ).map { it.into(Service::class.java) }
    }

    fun DSLContext.updateServiceById(service: Service): Mono<Service> {
        return Mono.from(
            update(SERVICE)
                .set(SERVICE.CATEGORY_ID, service.categoryId)
                .where(SERVICE.ID.eq(service.id))
                .returning()
        ).map { it.into(Service::class.java) }
    }
}