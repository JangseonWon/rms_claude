package com.gcgenome.rms.dao

import com.gcgenome.rms.data.RequestDTO
import com.gcgenome.rms.tables.references.REQUEST
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.time.LocalDateTime

interface RequestDao {
    fun DSLContext.insertRequest(request: RequestDTO): Mono<RequestDTO> {
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
        ).map { it.into(RequestDTO::class.java) }
    }
}