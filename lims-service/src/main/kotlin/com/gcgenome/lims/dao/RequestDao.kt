package com.gcgenome.lims.dao

import com.gcgenome.lims.data.Report
import com.gcgenome.lims.data.Request
import com.gcgenome.rms.tables.references.REQUEST
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.util.UUID

interface RequestDao {
    fun DSLContext.selectRequestBySampleIdAndServiceId(sampleId: UUID, serviceId: String): Mono<Report> {
        return Mono.from(
            selectFrom(REQUEST)
                .where(REQUEST.SERVICE_ID.eq(serviceId))
                .and(REQUEST.SAMPLE_ID.eq(sampleId))
        ).map { it.into(Report::class.java) }
    }

    fun DSLContext.updateRequestStatusById(orderId: UUID, serviceId: String, sampleId: UUID): Mono<Request> {
        return Mono.from(
            update(REQUEST)
                .set(REQUEST.STATUS, "DELIVERED")
                .where(REQUEST.ORDER_ID.eq(orderId))
                .and(REQUEST.SERVICE_ID.eq(serviceId))
                .and(REQUEST.SAMPLE_ID.eq(sampleId))
                .returning()
        ).map { it.into(Request::class.java) }
    }
}