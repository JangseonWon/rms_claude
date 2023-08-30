package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Service
import com.gcgenome.rms.tables.references.SERVICE
import org.jooq.DSLContext
import reactor.core.publisher.Mono


interface ServiceDao{
    fun DSLContext.selectServiceById(itemId: String): Mono<Service> {
        return Mono.from(selectFrom(SERVICE).where(SERVICE.ID.eq(itemId)))
            .map { it.into(Service::class.java) }
    }
}