package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Service_
import com.gcgenome.rms.tables.pojos.Service
import com.gcgenome.rms.tables.references.SERVICE
import com.gcgenome.rms.tables.references.USER_SERVICE
import org.jooq.Condition
import org.jooq.DSLContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


interface ServiceDao{
    fun DSLContext.selectServiceByUserId(userId: String): Flux<Service_>{
        return Flux.from(
            select(
                SERVICE.ID,
                SERVICE.NAME
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
                .set(SERVICE.NAME, service.name)
                .set(SERVICE.CATEGORY_ID, service.categoryId)
                .where(SERVICE.ID.eq(service.id))
                .returning()
        ).map { it.into(Service::class.java) }
    }
}