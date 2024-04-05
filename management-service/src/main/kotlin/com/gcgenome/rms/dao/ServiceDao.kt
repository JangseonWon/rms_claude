package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Service
import com.gcgenome.rms.tables.references.*
import org.jooq.DSLContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


interface ServiceDao{
    fun DSLContext.selectServiceByUserId(userId: String): Flux<Service>{
        return Flux.from(
            select(
                SERVICE.ID,
                SERVICE.NAME
            ).from(SERVICE)
                .join(USER_SERVICE).on(SERVICE.ID.eq(USER_SERVICE.SERVICE_ID))
                .where(USER_SERVICE.USER_ID.eq(userId))
        ).map { it.into(Service::class.java) }
    }
    fun DSLContext.selectServiceById(serviceId: String): Mono<Service> {
        return Mono.from(
            selectFrom(SERVICE).where(SERVICE.ID.eq(serviceId))
        ).map { it.into(Service::class.java) }
    }
}