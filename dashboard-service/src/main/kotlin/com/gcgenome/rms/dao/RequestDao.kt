package com.gcgenome.rms.dao

import com.gcgenome.rms.authentication.User
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.Request
import com.gcgenome.rms.data.Status
import com.gcgenome.rms.data.StatusCount
import com.gcgenome.rms.tables.references.*
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface RequestDao {
    fun DSLContext.selectStatusCount(user: User): Mono<StatusCount> {
        return Mono.from(
            select(
                count().filterWhere("status != 'CART'"),
                count().filterWhere("status = 'ORDERED'"),
                count().filterWhere("status = 'SPECIFIED'"),
                count().filterWhere("status = 'INPROGRESS'"),
                count().filterWhere("status = 'TESTFAILED'"),
                count().filterWhere("status = 'DELIVERED'"),
                count().filterWhere("status = 'FINISHED'")
            )
                .from(REQUEST)
                .join(ORDER).on(REQUEST.ORDER_ID.eq(ORDER.ID))
                .where().apply {
                    when(user.role) {
                        "USER" -> and(ORDER.USER_ID.eq(user.id))
                    }
                }
        ).map(StatusCount::toModel)
    }
    fun DSLContext.selectRequestCountByUserId(user: User, condition: Condition): Mono<Int> {
        return Mono.from(
            selectCount()
                .from(REQUEST)
                .join(ORDER).on(REQUEST.ORDER_ID.eq(ORDER.ID))
                .join(SERVICE).on(REQUEST.SERVICE_ID.eq(SERVICE.ID))
                .join(SAMPLE).on(REQUEST.SAMPLE_ID.eq(SAMPLE.ID))
                .join(PATIENT).on(SAMPLE.PATIENT_SERIAL.eq(PATIENT.SERIAL)
                    .and(SAMPLE.ORGANIZATION_ID.eq(PATIENT.ORGANIZATION_ID))
                    .and(SAMPLE.USER_ID.eq(PATIENT.USER_ID)))
                .where(REQUEST.STATUS.ne(Status.CART.toString())
                .and(`when`(`val`(user.role).eq("USER"), ORDER.USER_ID.eq(user.id)).else_(true)))
                .and(condition)
        ).map { it.into(Int::class.java) }
    }
    fun DSLContext.selectRequestByUserId(user: User, condition: Condition, query: Query): Flux<Request> {
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
                                key("id").value(ORGANIZATION.USER_ID)
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
            .join(SERVICE).on(REQUEST.SERVICE_ID.eq(SERVICE.ID))
            .where(REQUEST.STATUS.ne(Status.CART.toString())
                .and(`when`(`val`(user.role).eq("USER"), ORDER.USER_ID.eq(user.id)).else_(true))
                .and(condition)
            )
            .groupBy(REQUEST.ORDER_ID, REQUEST.SERVICE_ID, REQUEST.SAMPLE_ID,
                SAMPLE.ID,
                PATIENT.SERIAL, PATIENT.ORGANIZATION_ID, PATIENT.USER_ID,
                ORGANIZATION.ID, ORGANIZATION.USER_ID,
                SERVICE.ID
            )
            .orderBy(SAMPLE.BARCODE)
            .limit(query.page.size)
            .offset(query.page.number*query.page.size)

        ).map { it.into(Request::class.java) }
    }

}
