package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Request
import com.gcgenome.rms.data.Status
import com.gcgenome.rms.tables.references.REQUEST
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

interface RequestDao {

    fun DSLContext.selectRequestById(orderId: UUID, sampleId: UUID, serviceId: String): Mono<Request> {
        return Mono.from(selectFrom(REQUEST).where(REQUEST.ORDER_ID.eq(orderId).and(REQUEST.SAMPLE_ID.eq(sampleId))))
            .map { it.into(Request::class.java) }
    }

    fun DSLContext.insertRequest(request: Request): Mono<Request> {
        return Mono.from(
            insertInto(REQUEST)
                .set(REQUEST.ORDER_ID, request.orderId)
                .set(REQUEST.SERVICE_ID, request.service!!.id)
                .set(REQUEST.SAMPLE_ID, request.sample!!.id)
                .set(REQUEST.USER_SERVICE_ID, request.userServiceId ?: request.service.id)
                .set(REQUEST.STATUS, request.status)
                .set(REQUEST.MEMO, request.memo)
                .set(REQUEST.DEPARTMENT, request.department)
                .set(REQUEST.WARD, request.ward)
                .set(REQUEST.PHYSICIAN, request.physician)
                .set(REQUEST.CREATE_AT, request.status.takeIf { it != "CART" }?.let { LocalDateTime.now() })
                .set(REQUEST.CART_AT, request.status.takeIf { it == "CART" }?.let { LocalDateTime.now() })
                .set(REQUEST.LAST_MODIFY_AT, LocalDateTime.now())
                .returning()
        ).map { it.into(Request::class.java) }
    }
}