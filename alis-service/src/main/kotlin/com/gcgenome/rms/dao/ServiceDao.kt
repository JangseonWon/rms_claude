package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Service
import com.gcgenome.rms.tables.references.SERVICE
import org.jooq.DSLContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


interface ServiceDao{
    fun DSLContext.syncServiceByResponseBody(services: List<Service>): Flux<Service> {
        return Flux.fromIterable(services)
            .flatMap { service ->
                selectServiceById(service.id)
                    .switchIfEmpty(insertService(service))
            }
    }

    fun DSLContext.selectServiceById(serviceId: String): Mono<Service> {
        return Mono.from(
            selectFrom(SERVICE).where(SERVICE.ID.eq(serviceId))
        ).map { it.into(Service::class.java) }
    }

    fun DSLContext.insertService(service: Service): Mono<Service> {
        return Mono.from(
            insertInto(SERVICE)
                .set(SERVICE.ID, service.id)
                .set(SERVICE.NAME, service.name)
                .returning()
        ).map { it.into(Service::class.java) }
    }
}