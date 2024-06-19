package com.gcgenome.rms.dao

import com.gcgenome.rms.authentication.User
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.Request
import com.gcgenome.rms.data.Role
import com.gcgenome.rms.data.Status
import com.gcgenome.rms.tables.references.*
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

interface RequestDao {
    fun DSLContext.selectRequestCountByUserId(user: User): Mono<Int> {
        return Mono.from(
            selectCount()
                .from(REQUEST)
                .join(ORDER).on(REQUEST.ORDER_ID.eq(ORDER.ID))
                .where(REQUEST.STATUS.eq(Status.CART.toString())
                    .and(`when`(`val`(user.role).eq("USER"), ORDER.USER_ID.eq(user.id)).else_(true)))

        ).map { it.into(Int::class.java) }
    }
    fun DSLContext.updateRequestStatusAndCreateAtById(orderId: UUID, sampleId: UUID, serviceId: String):Mono<Request> {
        return Mono.from(
            update(REQUEST)
                .set(REQUEST.CREATE_AT, LocalDateTime.now())
                .set(REQUEST.STATUS, Status.ORDERED.toString())
                .where(REQUEST.ORDER_ID.eq(orderId)
                    .and(REQUEST.SERVICE_ID.eq(serviceId))
                    .and(REQUEST.SAMPLE_ID.eq(sampleId))
                )
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
                .set(REQUEST.LAST_MODIFY_AT, LocalDateTime.now())
                .where(REQUEST.ORDER_ID.eq(request.orderId).and(REQUEST.SAMPLE_ID.eq(request.sampleId).and(REQUEST.SERVICE_ID.eq(request.serviceId))))
                .returning()
        ).map { it.into(Request::class.java) }
    }
    fun DSLContext.deleteRequest(request: Request): Mono<Request> {
        return Mono.from(
            deleteFrom(REQUEST).where(
                REQUEST.ORDER_ID.eq(request.orderId)
                    .and(REQUEST.SAMPLE_ID.eq(request.sample!!.id))
                    .and(REQUEST.SERVICE_ID.eq(request.service!!.id))
            ).returning()
        ).map { it.into(Request::class.java) }
    }
    fun DSLContext.selectRequestByUserId(user: User, query: Query): Flux<Request> {
        return Flux.from(
            select(
                REQUEST.ORDER_ID,
                REQUEST.USER_SERVICE_ID,
                REQUEST.STATUS,
                REQUEST.MEMO,
                REQUEST.DEPARTMENT,
                REQUEST.WARD,
                REQUEST.PHYSICIAN,
                REQUEST.CREATE_AT,
                jsonObject(
                    key("id").value(SERVICE.ID),
                    key("name").value(SERVICE.NAME),
                    key("categoryId").value(SERVICE.CATEGORY_ID)
                ).`as`("service"),
                jsonObject(
                    key("id").value(SAMPLE.ID),
                    key("barcode").value(SAMPLE.BARCODE),
                    key("userSampleId").value(SAMPLE.USER_SAMPLE_ID),
                    key("quantity").value(SAMPLE.QUANTITY),
                    key("age").value(SAMPLE.AGE),
                    key("samplingOn").value(SAMPLE.SAMPLING_ON),
                    key("resampleReason").value(SAMPLE.RESAMPLE_REASON),
                    key("createAt").value(SAMPLE.CREATE_AT),
                    key("patient").value(jsonObject(
                        key("serial").value(PATIENT.SERIAL),
                        key("name").value(PATIENT.NAME),
                        key("sex").value(PATIENT.SEX),
                        key("birthYear").value(PATIENT.BIRTH_YEAR),
                        key("birthMonth").value(PATIENT.BIRTH_MONTH),
                        key("birthDay").value(PATIENT.BIRTH_DAY),
                        key("organization").value(jsonObject(
                            key("id").value(ORGANIZATION.ID),
                            key("name").value(ORGANIZATION.NAME),
                            key("type").value(ORGANIZATION.TYPE),
                            key("registrationNumber").value(ORGANIZATION.REGISTRATION_NUMBER),
                            key("nursingNumber").value(ORGANIZATION.NURSING_NUMBER),
                            key("user").value(jsonObject(
                                key("id").value(USER.ID),
                                key("branchName").value(USER.BRANCH_NAME),
                                key("branchSerial").value(USER.BRANCH_SERIAL)
                            ))
                        ))
                    ))
                ).`as`("sample")
            ).from(REQUEST)
            .join(ORDER).on(REQUEST.ORDER_ID.eq(ORDER.ID))
            .join(SAMPLE).on(REQUEST.SAMPLE_ID.eq(SAMPLE.ID))
            .join(PATIENT).on(
                SAMPLE.PATIENT_SERIAL.eq(PATIENT.SERIAL)
                    .and(SAMPLE.ORGANIZATION_ID.eq(PATIENT.ORGANIZATION_ID))
                    .and(SAMPLE.USER_ID.eq(PATIENT.USER_ID)))
            .join(ORGANIZATION).on(
                PATIENT.ORGANIZATION_ID.eq(ORGANIZATION.ID)
                    .and(PATIENT.USER_ID.eq(ORGANIZATION.USER_ID)))
            .join(USER).on(ORGANIZATION.USER_ID.eq(USER.ID))
            .join(SERVICE).on(REQUEST.SERVICE_ID.eq(SERVICE.ID))
            .where(
                REQUEST.STATUS.eq(Status.CART.toString())
                    .and(`when`(`val`(user.role).eq(Role.USER.toString()), ORDER.USER_ID.eq(user.id)).else_(true))
            )
            .groupBy(REQUEST.ORDER_ID, REQUEST.SERVICE_ID, REQUEST.SAMPLE_ID,
                SAMPLE.ID,
                PATIENT.SERIAL, PATIENT.ORGANIZATION_ID, PATIENT.USER_ID,
                ORGANIZATION.ID, ORGANIZATION.USER_ID, USER.ID,
                SERVICE.ID
            )
            .orderBy(SAMPLE.BARCODE)
            .limit(query.page.size)
            .offset(query.page.number*query.page.size)

        ).map { it.into(Request::class.java) }
    }

    fun DSLContext.selectRequestById(orderId: UUID, sampleId: UUID, serviceId: String): Mono<Request> {
        return Mono.from(
            select(
                REQUEST.ORDER_ID,
                REQUEST.USER_SERVICE_ID,
                REQUEST.MEMO,
                REQUEST.DEPARTMENT,
                REQUEST.WARD,
                REQUEST.PHYSICIAN,
                REQUEST.CART_AT,
                jsonObject(
                    key("id").value(SERVICE.ID),
                    key("name").value(SERVICE.NAME)
                ).`as`("service"),
                jsonObject(
                    key("id").value(SAMPLE.ID),
                    key("userSampleId").value(SAMPLE.USER_SAMPLE_ID),
                    key("quantity").value(SAMPLE.QUANTITY),
                    key("age").value(SAMPLE.AGE),
                    key("samplingOn").value(SAMPLE.SAMPLING_ON),
                    key("createAt").value(SAMPLE.CREATE_AT),
                    key("sampleType").value(jsonObject(
                        key("id").value(SAMPLE_TYPE.ID),
                        key("name").value(SAMPLE_TYPE.NAME)
                    )),
                    key("patient").value(jsonObject(
                        key("serial").value(PATIENT.SERIAL),
                        key("name").value(PATIENT.NAME),
                        key("sex").value(PATIENT.SEX),
                        key("birthYear").value(PATIENT.BIRTH_YEAR),
                        key("birthMonth").value(PATIENT.BIRTH_MONTH),
                        key("birthDay").value(PATIENT.BIRTH_DAY),
                        key("organization").value(jsonObject(
                            key("id").value(ORGANIZATION.ID),
                            key("name").value(ORGANIZATION.NAME),
                            key("registrationNumber").value(ORGANIZATION.REGISTRATION_NUMBER),
                            key("type").value(ORGANIZATION.TYPE),
                            key("nursingNumber").value(ORGANIZATION.NURSING_NUMBER),
                            key("user").value(jsonObject(
                                key("id").value(USER.ID)
                            ))
                        ))
                    )),
                    key("extensions").value(
                        select(
                            jsonArrayAgg(jsonObject(
                                key("id").value(EXTENSION.ID),
                                key("name").value(EXTENSION.NAME),
                                key("value").value(SAMPLE_EXTENSION.VALUE),
                                key("regex").value(EXTENSION.REGEX)
                            ))

                        ).from(SAMPLE_EXTENSION)
                            .join(EXTENSION).on(SAMPLE_EXTENSION.EXTENSION_ID.eq(EXTENSION.ID))
                            .where(SAMPLE_EXTENSION.SAMPLE_ID.eq(sampleId))
                    )
                ).`as`("sample")
            ).from(REQUEST)
                .join(SERVICE).on(REQUEST.SERVICE_ID.eq(SERVICE.ID))
                .join(SAMPLE).on(REQUEST.SAMPLE_ID.eq(SAMPLE.ID))
                .join(SAMPLE_TYPE).on(SAMPLE.SAMPLE_TYPE_ID.eq(SAMPLE_TYPE.ID))
                .join(PATIENT).on(
                    SAMPLE.PATIENT_SERIAL.eq(PATIENT.SERIAL)
                        .and(SAMPLE.ORGANIZATION_ID.eq(PATIENT.ORGANIZATION_ID))
                        .and(SAMPLE.USER_ID.eq(PATIENT.USER_ID))
                )
                .join(ORGANIZATION).on(
                    PATIENT.ORGANIZATION_ID.eq(ORGANIZATION.ID)
                        .and(PATIENT.USER_ID.eq(ORGANIZATION.USER_ID)))
                .join(USER).on(ORGANIZATION.USER_ID.eq(USER.ID))
                .where(
                    REQUEST.ORDER_ID.eq(orderId)
                        .and(REQUEST.SERVICE_ID.eq(serviceId))
                        .and(REQUEST.SAMPLE_ID.eq(sampleId))
                )

        ).map{it.into(Request::class.java)}
    }



}