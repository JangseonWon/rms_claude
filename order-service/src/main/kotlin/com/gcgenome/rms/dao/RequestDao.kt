package com.gcgenome.rms.dao

import com.gcgenome.rms.authentication.User
import com.gcgenome.rms.data.Page
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.Request
import com.gcgenome.rms.data.RequestDTO
import com.gcgenome.rms.tables.references.*
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import reactor.core.publisher.Mono
import java.util.*

interface RequestDao: QueryDao {
    fun DSLContext.selectOrderStatusRequestsWithPage(query: Query, userDto: User): Mono<Page<RequestDTO>> {
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
            ORDER.SERIAL.`as`("serial"),
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
                                            key("name").value(USER.NAME)
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
        }.and(REQUEST.STATUS.eq("ORDERED"))

        return selectPage(mainTable = REQUEST, query = query,
            joinTables = joins, selectFields = fields, where = finalCondition) { record ->
            record.into(RequestDTO::class.java)
        }
    }

    fun DSLContext.selectRequestByPK(sampleId: UUID, serviceId: String): Mono<Request> =
        Mono.from(
            select(
                jsonObject(
                    SERVICE.ID,
                    SERVICE.NAME,
                    SERVICE.CATEGORY_ID
                ).`as`("service"),
                ORDER.SERIAL,
                ORDER.ID,
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
                                    key("user").value(jsonObject(
                                        key("id").value(ORGANIZATION.USER_ID)
                                    ))
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
                .where(REQUEST.SAMPLE_ID.eq(sampleId)
                    .and(REQUEST.SERVICE_ID.eq(serviceId)))
        ).map { record -> Request.toPatientModel(record) }
}