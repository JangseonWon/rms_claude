package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Service
import com.gcgenome.rms.tables.records.ServiceRecord
import com.gcgenome.rms.tables.references.SERVICE
import com.gcgenome.rms.tables.references.USER_SERVICE
import org.jooq.Condition
import org.jooq.DSLContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface ServiceDao  {
    fun DSLContext.selectServiceByUserIdAndCategoryId(andWhere: Condition, categoryId: UUID): Flux<Service> {
        return Flux.from(
            select(
                SERVICE.ID,
                SERVICE.NAME
            ).from(SERVICE)
                .join(USER_SERVICE).on(SERVICE.ID.eq(USER_SERVICE.SERVICE_ID))
                .where(SERVICE.CATEGORY_ID.eq(categoryId).and(andWhere))
        ).map { it.into(Service::class.java) }
    }

    fun DSLContext.checkServiceId(serviceId: String): Mono<ServiceRecord> {
        return Mono.from(
            selectFrom(SERVICE).where(SERVICE.ID.eq(serviceId))
        )
    }
}