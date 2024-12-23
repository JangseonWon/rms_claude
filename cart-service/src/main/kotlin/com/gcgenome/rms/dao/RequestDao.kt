package com.gcgenome.rms.dao

import com.gcgenome.rms.authentication.User
import com.gcgenome.rms.data.*
import com.gcgenome.rms.tables.references.*
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

interface RequestDao: QueryDao {
    fun DSLContext.selectRequestCartByUserId(userDto: User, query: Query): Mono<Page<RequestDTO>> {
        val joins = listOf(
            QueryDao.JoinInfo(ORDER, REQUEST.ORDER_ID.eq(ORDER.ID), QueryDao.JoinType.INNER),
            QueryDao.JoinInfo(SAMPLE, REQUEST.SAMPLE_ID.eq(SAMPLE.ID), QueryDao.JoinType.INNER),
            QueryDao.JoinInfo(PATIENT, SAMPLE.PATIENT_SERIAL.eq(PATIENT.SERIAL)
                .and(SAMPLE.ORGANIZATION_ID.eq(PATIENT.ORGANIZATION_ID))
                .and(SAMPLE.USER_ID.eq(PATIENT.USER_ID)), QueryDao.JoinType.INNER),
            QueryDao.JoinInfo(SERVICE, REQUEST.SERVICE_ID.eq(SERVICE.ID), QueryDao.JoinType.INNER),
            QueryDao.JoinInfo(SAMPLE_TYPE, SAMPLE.SAMPLE_TYPE_ID.eq(SAMPLE_TYPE.ID), QueryDao.JoinType.INNER),
            QueryDao.JoinInfo(ORGANIZATION, ORGANIZATION.ID.eq(PATIENT.ORGANIZATION_ID)
                .and(ORGANIZATION.USER_ID.eq(PATIENT.USER_ID)), QueryDao.JoinType.INNER),
            QueryDao.JoinInfo(USER, USER.ID.eq(PATIENT.USER_ID), QueryDao.JoinType.INNER)
        )

        val fields = listOf(
            jsonObject(
                SERVICE.ID,
                SERVICE.NAME,
                SERVICE.CATEGORY_ID
            ).`as`("service"),
            ORDER.ID.`as`("order_id"),
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
            jsonObject(
                key("id").value(SAMPLE.ID),
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
                                key("nursing_number").value(ORGANIZATION.NURSING_NUMBER),
                                key("user").value(
                                    select(
                                        jsonObject(
                                            key("id").value(USER.ID),
                                            key("name").value(USER.NAME),
                                            key("branch_serial").value(USER.BRANCH_SERIAL)
                                        )
                                    ).from(USER).where(ORGANIZATION.USER_ID.eq(USER.ID))
                                )
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
                )
            ).`as`("sample")
        )

        val baseCondition = REQUEST.ORDER_ID.eq(ORDER.ID)
            .and(REQUEST.SAMPLE_ID.eq(SAMPLE.ID))
            .and(REQUEST.SERVICE_ID.eq(SERVICE.ID))

        val finalCondition = if (userDto.role == "USER") {
            baseCondition.and(ORDER.USER_ID.eq(userDto.id))
        } else {
            baseCondition
        }.and(REQUEST.STATUS.eq("CART"))

        return selectPage(mainTable = REQUEST, query = query,
            joinTables = joins, selectFields = fields, where = finalCondition) { record ->
            record.into(RequestDTO::class.java)
        }
    }

    fun DSLContext.updateRequestStatusAndCreateAtById(orderId: UUID, sampleId: UUID, serviceId: String):Mono<Request> {
        return Mono.from(
            update(REQUEST)
                .set(REQUEST.CREATE_AT, LocalDateTime.now())
                .set(REQUEST.STATUS, Status.UNCONFIRMED_ORDER.toString())
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

    fun DSLContext.selectRequestById(sampleId: UUID, serviceId: String): Mono<Request> {
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
                    REQUEST.SERVICE_ID.eq(serviceId)
                    .and(REQUEST.SAMPLE_ID.eq(sampleId))
                )

        ).map{it.into(Request::class.java)}
    }
}