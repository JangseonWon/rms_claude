package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.records.ServiceRecord
import com.gcgenome.rms.tables.references.SERVICE
import org.jooq.DSLContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface ServiceDao {

    fun DSLContext.selectServiceByCategoryId(categoryId: UUID): Flux<ServiceRecord> {
        return Flux.from(
            selectFrom(SERVICE).where(SERVICE.CATEGORY_ID.eq(categoryId))
        )
    }

    fun DSLContext.checkServiceId(serviceId: String): Mono<ServiceRecord> {
        return Mono.from(
            selectFrom(SERVICE).where(SERVICE.ID.eq(serviceId))
        )
    }

    fun DSLContext.checkCategoryId(categoryId: UUID): Mono<ServiceRecord> {
        return Mono.from(
            selectFrom(SERVICE).where(SERVICE.CATEGORY_ID.eq(categoryId))
        )
    }
}