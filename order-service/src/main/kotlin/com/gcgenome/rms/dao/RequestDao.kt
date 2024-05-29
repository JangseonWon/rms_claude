package com.gcgenome.rms.dao

import com.gcgenome.rms.authentication.User
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.tables.references.*
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import org.jooq.SortOrder
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import com.gcgenome.rms.data.Request
import com.gcgenome.rms.data.Status
import com.gcgenome.rms.tables.records.RequestRecord
import com.gcgenome.rms.tables.references.REQUEST
import java.time.LocalDateTime
import java.util.*

interface RequestDao {

    fun DSLContext.selectRequests(query: Query, where: Condition, userDto: User): Flux<Request> {
        val asc : SortOrder = when (query.asc) {
            true -> SortOrder.ASC
            false -> SortOrder.DESC
        }

        return Flux.from(
            select(
                jsonObject(
                    SERVICE.ID,
                    SERVICE.NAME,
                    SERVICE.CATEGORY_ID
                ).`as`("service"),
                REQUEST.USER_SERVICE_ID,
                REQUEST.STATUS,
                REQUEST.MEMO,
                REQUEST.DEPARTMENT,
                REQUEST.WARD,
                REQUEST.PHYSICIAN,
                REQUEST.CREATE_AT,
                REQUEST.CART_AT,
                REQUEST.SPECIFIED_AT,
                REQUEST.COMPLETE_AT,
                REQUEST.RESAMPLE_AT,
                REQUEST.LAST_MODIFY_AT,
                REQUEST.EMP_ID,
                REQUEST.EMP_NAME,
                REQUEST.EMP_MOBILE,
                REQUEST.TEST,
                REQUEST.CREDIT,
                REQUEST.PRICE,
                REQUEST.OUTSOURCING_COST,
                jsonObject(
                    key("barcode").value(SAMPLE.BARCODE),
                    key("user_sample_id").value(SAMPLE.USER_SAMPLE_ID),
                    key("quantity").value(SAMPLE.QUANTITY),
                    key("age").value(SAMPLE.AGE),
                    key("resample_reason").value(SAMPLE.RESAMPLE_REASON),
                    key("sampling_on").value(SAMPLE.SAMPLING_ON),
                    key("create_at").value(SAMPLE.CREATE_AT),
                    key("sample_type").value(
                        jsonObject(
                            key("id").value(SAMPLE_TYPE.ID),
                            key("name").value(SAMPLE_TYPE.NAME)
                        )
                    ),
                    key("patient").value(
                        jsonObject(
                            key("serial").value(PATIENT.SERIAL),
                            key("name").value(PATIENT.NAME),
                            key("sex").value(PATIENT.SEX),
                            key("birth_year").value(PATIENT.BIRTH_YEAR),
                            key("birth_month").value(PATIENT.BIRTH_MONTH),
                            key("birth_day").value(PATIENT.BIRTH_DAY),
                            key("organization").value(
                                jsonObject(
                                    key("id").value(ORGANIZATION.ID),
                                    key("name").value(ORGANIZATION.NAME),
                                    key("type").value(ORGANIZATION.TYPE),
                                    key("registration_number").value(ORGANIZATION.REGISTRATION_NUMBER),
                                    key("nursing_number").value(ORGANIZATION.NURSING_NUMBER)
                                )
                            )
                        )
                    ),
                    key("extensions").value(
                        select(
                            jsonArrayAgg(
                                jsonObject(
                                    key("id").value(SAMPLE_EXTENSION.EXTENSION_ID),
                                    key("value").value(SAMPLE_EXTENSION.VALUE)
                                )
                            )
                        ).from(SAMPLE_EXTENSION).where(SAMPLE.ID.eq(SAMPLE_EXTENSION.SAMPLE_ID))
                    ),
                ).`as`("sample")
            ).from(ORDER)
                .join(REQUEST).on(ORDER.ID.eq(REQUEST.ORDER_ID))
                .join(SAMPLE).on(REQUEST.SAMPLE_ID.eq(SAMPLE.ID))
                .join(PATIENT).on(
                    SAMPLE.PATIENT_SERIAL.eq(PATIENT.SERIAL)
                        .and(
                            SAMPLE.ORGANIZATION_ID.eq(PATIENT.ORGANIZATION_ID)
                                .and(SAMPLE.USER_ID.eq(PATIENT.USER_ID))
                        )
                )
                .join(SERVICE).on(REQUEST.SERVICE_ID.eq(SERVICE.ID))
                .join(SAMPLE_TYPE).on(SAMPLE.SAMPLE_TYPE_ID.eq(SAMPLE_TYPE.ID))
                .join(ORGANIZATION).on(ORGANIZATION.ID.eq(PATIENT.ORGANIZATION_ID)
                    .and(ORGANIZATION.USER_ID.eq(PATIENT.USER_ID)))
                .where(where).and(REQUEST.ORDER_ID.eq(ORDER.ID)
                    .and(REQUEST.SAMPLE_ID.eq(SAMPLE.ID))
                    .and(REQUEST.SERVICE_ID.eq(SERVICE.ID)))
                .apply {
                    when { userDto.role.equals("USER") -> and(ORDER.USER_ID.eq(userDto.id)) }
                }
                .orderBy(field(query.sortBy).sort(asc))
                .limit(query.size)
                .offset(query.page*query.size)
        ).map (Request::toPatientModel)
    }

    fun DSLContext.selectRequestsCount(query: Query, where: Condition, userDto: User): Mono<Int> {
        return Mono.from(
            select(count())
                .from(REQUEST)
                .join(ORDER).on(REQUEST.ORDER_ID.eq(ORDER.ID))
                .join(SAMPLE).on(REQUEST.SAMPLE_ID.eq(SAMPLE.ID))
                .join(PATIENT).on(SAMPLE.PATIENT_SERIAL.eq(PATIENT.SERIAL).and(SAMPLE.ORGANIZATION_ID.eq(PATIENT.ORGANIZATION_ID).and(
                    SAMPLE.USER_ID.eq(PATIENT.USER_ID))))
                .join(SERVICE).on(REQUEST.SERVICE_ID.eq(SERVICE.ID))
                .join(SAMPLE_TYPE).on(SAMPLE.SAMPLE_TYPE_ID.eq(SAMPLE_TYPE.ID))
                .where(where).apply {
                    when { userDto.role.equals("USER") -> and(ORDER.USER_ID.eq(userDto.id)) }
                }
        )
            .map { it.component1() }
    }

    fun DSLContext.selectRequestById(orderId: UUID, serviceId: String, sampleId: UUID): Mono<Request> {
        return Mono.from(selectFrom(REQUEST)
            .where(REQUEST.ORDER_ID.eq(orderId)
                .and(REQUEST.SAMPLE_ID.eq(sampleId))
                .and(REQUEST.SERVICE_ID.eq(serviceId))
            ))
            .map { it.into(Request::class.java) }
    }

    fun DSLContext.insertRequest(requestDto: Request): Mono<Request> {
        return Mono.from(
            insertInto(REQUEST)
                .set(REQUEST.ORDER_ID, requestDto.orderId)
                .set(REQUEST.SERVICE_ID, requestDto.service!!.id)
                .set(REQUEST.SAMPLE_ID, requestDto.sampleId)
                .set(REQUEST.USER_SERVICE_ID, requestDto.userServiceId ?: requestDto.service.id)
                .set(REQUEST.STATUS, Status.ORDERED.toString())
                .set(REQUEST.MEMO, requestDto.memo)
                .set(REQUEST.DEPARTMENT, requestDto.department)
                .set(REQUEST.WARD, requestDto.ward)
                .set(REQUEST.PHYSICIAN, requestDto.physician)
                .set(REQUEST.CREATE_AT, LocalDateTime.now())
                .set(REQUEST.CART_AT, requestDto.cartAt)
                .set(REQUEST.SPECIFIED_AT, requestDto.specifiedAt)
                .set(REQUEST.COMPLETE_AT, requestDto.completeAt)
                .set(REQUEST.RESAMPLE_AT, requestDto.resampleAt)
                .set(REQUEST.LAST_MODIFY_AT, LocalDateTime.now())
                .set(REQUEST.EMP_ID, requestDto.empId)
                .set(REQUEST.EMP_NAME, requestDto.empName)
                .set(REQUEST.EMP_MOBILE, requestDto.empMobile)
                .set(REQUEST.TEST, requestDto.test)
                .set(REQUEST.CREDIT, requestDto.credit)
                .set(REQUEST.PRICE, requestDto.price)
                .set(REQUEST.OUTSOURCING_COST, requestDto.outsourcingCost)
                .returning()
        ).map { it.into(Request::class.java) }
    }

    fun DSLContext.updateRequest(request: Request): Mono<Request> {
        return Mono.from(
            update(REQUEST)
                .set(REQUEST.USER_SERVICE_ID, coalesce(`val`(request.userServiceId), REQUEST.USER_SERVICE_ID))
                .set(REQUEST.STATUS, coalesce(`val`(request.status), REQUEST.STATUS))
                .set(REQUEST.MEMO, coalesce(`val`(request.memo), REQUEST.MEMO))
                .set(REQUEST.DEPARTMENT, coalesce(`val`(request.department), REQUEST.DEPARTMENT))
                .set(REQUEST.WARD, coalesce(`val`(request.ward), REQUEST.WARD))
                .set(REQUEST.PHYSICIAN, coalesce(`val`(request.physician), REQUEST.PHYSICIAN))
                .set(REQUEST.LAST_MODIFY_AT,LocalDateTime.now())
                .set(REQUEST.EMP_ID, coalesce(`val`(request.empId),REQUEST.EMP_ID))
                .set(REQUEST.EMP_NAME, coalesce(`val`(request.empName),REQUEST.EMP_NAME))
                .set(REQUEST.EMP_MOBILE, coalesce(`val`(request.empMobile), REQUEST.EMP_MOBILE))
                .set(REQUEST.TEST, coalesce(`val`(request.test),REQUEST.TEST))
                .set(REQUEST.CREDIT, coalesce(`val`(request.credit), REQUEST.CREDIT))
                .set(REQUEST.PRICE, coalesce(`val`(request.price), REQUEST.PRICE))
                .set(REQUEST.OUTSOURCING_COST, coalesce(`val`(request.outsourcingCost), REQUEST.OUTSOURCING_COST))
                .where(REQUEST.ORDER_ID.eq(request.orderId).and(REQUEST.SAMPLE_ID.eq(request.sampleId).and(REQUEST.SERVICE_ID.eq(request.service!!.id))))
                .returning()
        ).map { it.into(Request::class.java) }
    }

    fun DSLContext.deleteRequestById(orderId: UUID, sampleId: UUID, serviceId: String): Mono<RequestRecord> {
        return Mono.from(
            deleteFrom(REQUEST)
                .where(REQUEST.ORDER_ID.eq(orderId)
                    .and(REQUEST.SAMPLE_ID.eq(sampleId))
                    .and(REQUEST.SERVICE_ID.eq(serviceId)))
                .returning()
        )
    }

    fun DSLContext.selectRequestByPK(orderId: UUID, sampleId: UUID, serviceId: String): Mono<Request> =
        Mono.from(
            select(
                jsonObject(
                    SERVICE.ID,
                    SERVICE.NAME,
                    SERVICE.CATEGORY_ID
                ).`as`("service"),
                REQUEST.USER_SERVICE_ID,
                REQUEST.STATUS,
                REQUEST.MEMO,
                REQUEST.DEPARTMENT,
                REQUEST.WARD,
                REQUEST.PHYSICIAN,
                REQUEST.CREATE_AT,
                REQUEST.CART_AT,
                REQUEST.SPECIFIED_AT,
                REQUEST.COMPLETE_AT,
                REQUEST.RESAMPLE_AT,
                REQUEST.LAST_MODIFY_AT,
                REQUEST.EMP_ID,
                REQUEST.EMP_NAME,
                REQUEST.EMP_MOBILE,
                REQUEST.TEST,
                REQUEST.CREDIT,
                REQUEST.PRICE,
                REQUEST.OUTSOURCING_COST,
                jsonObject(
                    key("serial").value(PATIENT.SERIAL),
                    key("sex").value(PATIENT.SEX),
                    key("name").value(PATIENT.NAME),
                    key("birth_year").value(PATIENT.BIRTH_YEAR),
                    key("birth_month").value(PATIENT.BIRTH_MONTH),
                    key("birth_day").value(PATIENT.BIRTH_DAY),
                    key("organization").value(
                        select(
                            jsonObject(
                                key("id").value(ORGANIZATION.ID),
                                key("name").value(ORGANIZATION.NAME),
                                key("type").value(ORGANIZATION.TYPE),
                                key("registration_number").value(ORGANIZATION.REGISTRATION_NUMBER),
                                key("nursing_number").value(ORGANIZATION.NURSING_NUMBER)
                            )
                        ).from(ORGANIZATION)
                            .where(
                                PATIENT.ORGANIZATION_ID.eq(ORGANIZATION.ID)
                                    .and(PATIENT.USER_ID.eq(ORGANIZATION.USER_ID))
                            )
                    ),
                    key("sample").value(
                        select(
                            jsonObject(
                                key("barcode").value(SAMPLE.BARCODE),
                                key("user_sample_id").value(SAMPLE.USER_SAMPLE_ID),
                                key("quantity").value(SAMPLE.QUANTITY),
                                key("age").value(SAMPLE.AGE),
                                key("sampling_on").value(SAMPLE.SAMPLING_ON),
                                key("resample_reason").value(SAMPLE.RESAMPLE_REASON),
                                key("create_at").value(SAMPLE.CREATE_AT),
                                key("sample_type_id").value(SAMPLE.SAMPLE_TYPE_ID),
                                key("patient_serial").value(SAMPLE.PATIENT_SERIAL),
                                key("extensions").value(
                                    select(
                                        jsonArrayAgg(
                                            jsonObject(
                                                key("id").value(SAMPLE_EXTENSION.EXTENSION_ID),
                                                key("value").value(SAMPLE_EXTENSION.VALUE)
                                            )
                                        )
                                    ).from(SAMPLE_EXTENSION)
                                        .where(SAMPLE.ID.eq(SAMPLE_EXTENSION.SAMPLE_ID))
                                )
                            )
                        ).from(SAMPLE).where(REQUEST.SAMPLE_ID.eq(SAMPLE.ID))
                    )
                ).`as`("patient")
            ).from(ORDER)
                .join(REQUEST).on(ORDER.ID.eq(REQUEST.ORDER_ID))
                .join(SAMPLE).on(REQUEST.SAMPLE_ID.eq(SAMPLE.ID))
                .join(PATIENT).on(
                    SAMPLE.PATIENT_SERIAL.eq(PATIENT.SERIAL)
                        .and(
                            SAMPLE.ORGANIZATION_ID.eq(PATIENT.ORGANIZATION_ID)
                                .and(SAMPLE.USER_ID.eq(PATIENT.USER_ID))
                        )
                )
                .join(SERVICE).on(REQUEST.SERVICE_ID.eq(SERVICE.ID))
                .where(REQUEST.ORDER_ID.eq(orderId)
                    .and(REQUEST.SAMPLE_ID.eq(sampleId))
                    .and(REQUEST.SERVICE_ID.eq(serviceId)))
        ).map(Request::toPatientModel)
}