package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Request
import com.gcgenome.rms.tables.references.REQUEST
import org.jooq.DSLContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface RequestDao {
    fun DSLContext.selectRequestById(orderId:UUID, serviceId: String, sampleId: UUID): Mono<Request> {
        return Mono.from(
            selectFrom(REQUEST)
                .where(REQUEST.SERVICE_ID.eq(serviceId).and(REQUEST.SAMPLE_ID.eq(sampleId)).and(REQUEST.ORDER_ID.eq(orderId)))
        ).map { it.into(Request::class.java) }
    }

    fun DSLContext.selectRequestBySampleId(sampleId: UUID): Flux<Request> {
        return Flux.from(
            selectFrom(REQUEST)
                .where(REQUEST.SAMPLE_ID.eq(sampleId))
        ).map { it.into(Request::class.java) }
    }

    fun DSLContext.deleteRequestById(request: Request):Mono<Request> {
        return Mono.from(
            deleteFrom(REQUEST)
                .where(REQUEST.ORDER_ID.eq(request.orderId).and(REQUEST.SERVICE_ID.eq(request.serviceId)).and(REQUEST.SAMPLE_ID.eq(request.sampleId)))
                .returning()
        ).map { it.into(Request::class.java) }
    }

}