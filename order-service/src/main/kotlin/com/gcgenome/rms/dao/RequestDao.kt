package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Request
import com.gcgenome.rms.data.Dto
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

    fun DSLContext.insertRequest(sampleId: UUID, resampleId: Dto, request: Request): Mono<Request> {
        return Mono.from(
            insertInto(REQUEST)
                .set(REQUEST.ORDER_ID, resampleId.orderId)
                .set(REQUEST.SERVICE_ID, resampleId.serviceId)
                .set(REQUEST.SAMPLE_ID, sampleId)
                .set(REQUEST.USER_SERVICE_ID, request.userServiceId ?: resampleId.serviceId)
                .set(REQUEST.STATUS, Status.ORDERED.toString())
                .set(REQUEST.MEMO, request.memo)
                .set(REQUEST.DEPARTMENT, request.department)
                .set(REQUEST.WARD, request.ward)
                .set(REQUEST.PHYSICIAN, request.physician)
                .set(REQUEST.CREATE_AT, LocalDateTime.now())
                .set(REQUEST.CART_AT, request.cartAt)
                .set(REQUEST.SPECIFIED_AT, request.specifiedAt)
                .set(REQUEST.COMPLETE_AT, request.completeAt)
                .set(REQUEST.RESAMPLE_AT, request.resampleAt)
                .set(REQUEST.LAST_MODIFY_AT, LocalDateTime.now())
                .set(REQUEST.EMP_ID, request.empId)
                .set(REQUEST.EMP_NAME, request.empName)
                .set(REQUEST.EMP_MOBILE, request.empMobile)
                .set(REQUEST.TEST, request.test)
                .set(REQUEST.CREDIT, request.credit)
                .set(REQUEST.PRICE, request.price)
                .set(REQUEST.OUTSOURCING_COST, request.outsourcingCost)
                .returning()
        ).map { it.into(Request::class.java) }
    }
}
