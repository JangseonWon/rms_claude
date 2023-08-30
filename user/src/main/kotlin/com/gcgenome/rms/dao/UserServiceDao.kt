package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.Service
import com.gcgenome.rms.tables.references.*
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface UserServiceDao {
    fun DSLContext.selectUserServiceById(query: Query, userId: String): Flux<Service> {
        val query =
            select(
                SERVICE.ID,
                SERVICE.NAME,
                `when`(count(SERVICE_EXTENSION.EXTENSION_ID).greaterThan(0),
                    jsonArrayAgg(jsonObject(
                        key("id").value(EXTENSION.ID),
                        key("name").value(EXTENSION.NAME),
                        key("regex").value(EXTENSION.REGEX),
                        key("required").value(SERVICE_EXTENSION.REQUIRED),
                    ))
                ).`as`("extensions"),
                field(select(
                    jsonArrayAgg(jsonObject(
                        key("id").value(SAMPLE_TYPE.ID),
                        key("name").value(SAMPLE_TYPE.NAME)
                    ))
                ).from(SAMPLE_TYPE)
                    .join(SERVICE_SAMPLE_TYPE).on(SAMPLE_TYPE.ID.eq(SERVICE_SAMPLE_TYPE.SAMPLE_TYPE_ID))
                    .where(SERVICE.ID.eq(SERVICE_SAMPLE_TYPE.SERVICE_ID))
                ).`as`("sample_types")
            ).from(USER_SERVICE)
                .join(SERVICE).on(USER_SERVICE.SERVICE_ID.eq(SERVICE.ID))
                .fullOuterJoin(SERVICE_EXTENSION).on(SERVICE.ID.eq(SERVICE_EXTENSION.SERVICE_ID))
                .fullOuterJoin(EXTENSION).on(SERVICE_EXTENSION.EXTENSION_ID.eq(EXTENSION.ID))
                .where(USER_SERVICE.USER_ID.eq(userId))
                .groupBy(SERVICE.ID, SERVICE.NAME)
                .orderBy(SERVICE.ID)
                .limit(query.size)
                .offset(query.page*query.size)
        return Flux.from(query).map{it.into(Service::class.java)}
    }

    fun DSLContext.selectUserServiceCount(query: Query, userId: String): Mono<Int> {
        return Mono.from(selectCount().from(USER).where(USER.ID.eq(userId))).map { it.component1() }
    }

}