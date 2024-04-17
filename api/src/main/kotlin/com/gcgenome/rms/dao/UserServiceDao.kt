package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Service
import com.gcgenome.rms.data.UserService
import com.gcgenome.rms.tables.references.*
import org.jooq.DSLContext
import org.jooq.impl.DSL
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface UserServiceDao {

    fun DSLContext.selectUserServiceById(userId: String, serviceId: String): Mono<UserService> {
        return Mono.from(
            selectFrom(USER_SERVICE)
                .where(USER_SERVICE.USER_ID.eq(userId)
                    .and(USER_SERVICE.SERVICE_ID.eq(serviceId)))
        ).map { it.into(UserService::class.java) }
    }
    fun DSLContext.selectUserService(userId: String): Flux<Service> {
        return Flux.from(
            select(
                SERVICE.ID,
                SERVICE.NAME,
                DSL.`when`(
                    DSL.count(SERVICE_EXTENSION.EXTENSION_ID).greaterThan(0),
                    DSL.jsonArrayAgg(
                        DSL.jsonObject(
                            DSL.key("id").value(EXTENSION.ID),
                            DSL.key("name").value(EXTENSION.NAME),
                            DSL.key("regex").value(EXTENSION.REGEX),
                            DSL.key("required").value(SERVICE_EXTENSION.REQUIRED),
                        )
                    )
                ).`as`("extensions"),
                DSL.field(
                    select(
                        DSL.jsonArrayAgg(
                            DSL.jsonObject(
                                DSL.key("id").value(SAMPLE_TYPE.ID),
                                DSL.key("name").value(SAMPLE_TYPE.NAME)
                            )
                        )
                    ).from(SAMPLE_TYPE)
                        .join(SERVICE_SAMPLE_TYPE).on(SAMPLE_TYPE.ID.eq(SERVICE_SAMPLE_TYPE.SAMPLE_TYPE_ID))
                        .where(SERVICE.ID.eq(SERVICE_SAMPLE_TYPE.SERVICE_ID))
                ).`as`("sample_types")
            ).from(USER_SERVICE)
                .join(SERVICE).on(USER_SERVICE.SERVICE_ID.eq(SERVICE.ID))
                .leftOuterJoin(SERVICE_EXTENSION).on(SERVICE.ID.eq(SERVICE_EXTENSION.SERVICE_ID))
                .leftOuterJoin(EXTENSION).on(SERVICE_EXTENSION.EXTENSION_ID.eq(EXTENSION.ID))
                .where(USER_SERVICE.USER_ID.eq(userId))
                .groupBy(SERVICE.ID)
                .orderBy(SERVICE.ID)
        ).map { it.into(Service::class.java) }
    }
}