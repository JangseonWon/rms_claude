package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Request
import com.gcgenome.rms.data.Status
import com.gcgenome.rms.tables.references.REQUEST
import org.jooq.DSLContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime
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

    fun DSLContext.insertRequest(orderId: UUID, request: Request, sampleId: UUID): Mono<Request>{
        return Mono.from(
            insertInto(REQUEST)
                .set(REQUEST.ORDER_ID,orderId)
                .set(REQUEST.SERVICE_ID,request.service!!.id)
                .set(REQUEST.SAMPLE_ID,sampleId)
                .set(REQUEST.USER_SERVICE_ID,request.userServiceId)
                .set(REQUEST.STATUS, Status.ORDERED.toString())
                .set(REQUEST.MEMO, request.memo)
                .set(REQUEST.DEPARTMENT, request.department)
                .set(REQUEST.WARD, request.ward)
                .set(REQUEST.PHYSICIAN, request.physician)
                .set(REQUEST.CREATE_AT, LocalDateTime.now())
                .set(REQUEST.SPECIFIED_AT,LocalDateTime.now())
                .set(REQUEST.LAST_MODIFY_AT,LocalDateTime.now())
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

    fun DSLContext.deleteRequestById(request: Request):Mono<Request> {
        return Mono.from(
            deleteFrom(REQUEST)
                .where(REQUEST.ORDER_ID.eq(request.orderId).and(REQUEST.SERVICE_ID.eq(request.serviceId)).and(REQUEST.SAMPLE_ID.eq(request.sampleId)))
                .returning()
        ).map { it.into(Request::class.java) }
    }

}