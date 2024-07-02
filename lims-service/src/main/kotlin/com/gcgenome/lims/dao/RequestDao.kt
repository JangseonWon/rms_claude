package com.gcgenome.lims.dao

import com.gcgenome.lims.data.Request
import com.gcgenome.rms.tables.references.REQUEST
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.util.UUID

interface RequestDao {
    fun DSLContext.selectRequestBySampleIdAndServiceId(sampleId: UUID, serviceId: String): Mono<Request> {
        return Mono.from(
            selectFrom(REQUEST)
                .where(REQUEST.SERVICE_ID.eq(serviceId))
                .and(REQUEST.SAMPLE_ID.eq(sampleId))
        ).map { it.into(Request::class.java) }
    }

    fun DSLContext.updateRequestStatusById(orderId: UUID, serviceId: String, sampleId: UUID, status: String): Mono<Request> {
        return Mono.from(
            update(REQUEST)
                .set(REQUEST.STATUS, status)
                .where(REQUEST.ORDER_ID.eq(orderId))
                .and(REQUEST.SERVICE_ID.eq(serviceId))
                .and(REQUEST.SAMPLE_ID.eq(sampleId))
                .returning()
        ).map { it.into(Request::class.java) }
    }
}