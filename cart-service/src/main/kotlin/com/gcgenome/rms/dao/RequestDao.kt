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
    fun DSLContext.selectRequestsByUserId(userDto: User, query: Query): Mono<Page<RequestDTO>> {
        val joins = listOf(
            QueryDao.JoinInfo(USER, REQUEST.USER_ID.eq(USER.ID), QueryDao.JoinType.INNER),
            QueryDao.JoinInfo(SAMPLE, REQUEST.SAMPLE_ID.eq(SAMPLE.ID), QueryDao.JoinType.INNER),
            QueryDao.JoinInfo(SERVICE, REQUEST.SERVICE_ID.eq(SERVICE.ID), QueryDao.JoinType.INNER),
            QueryDao.JoinInfo(REQUEST_GROUP, REQUEST.REQUEST_GROUP_ID.eq(REQUEST_GROUP.ID), QueryDao.JoinType.INNER),
            QueryDao.JoinInfo(REQUEST_RELATION, REQUEST.REQUEST_RELATION_ID.eq(REQUEST_RELATION.ID), QueryDao.JoinType.INNER),
            QueryDao.JoinInfo(PATIENT, SAMPLE.PATIENT_SERIAL.eq(PATIENT.SERIAL)
                .and(SAMPLE.ORGANIZATION_ID.eq(PATIENT.ORGANIZATION_ID))
                .and(SAMPLE.USER_ID.eq(PATIENT.USER_ID)), QueryDao.JoinType.INNER),
            QueryDao.JoinInfo(SAMPLE_TYPE, SAMPLE.SAMPLE_TYPE_ID.eq(SAMPLE_TYPE.ID), QueryDao.JoinType.INNER),
            QueryDao.JoinInfo(ORGANIZATION, ORGANIZATION.ID.eq(PATIENT.ORGANIZATION_ID).and(ORGANIZATION.USER_ID.eq(PATIENT.USER_ID)), QueryDao.JoinType.INNER),
        )

        val fields = listOf(
            REQUEST.USER_SERVICE_ID,
            REQUEST.STATUS,
            REQUEST.MEMO,
            REQUEST.DEPARTMENT,
            REQUEST.WARD,
            REQUEST.PHYSICIAN,
            REQUEST.CREATE_AT,
            REQUEST.CART_AT,
            REQUEST.AWB_NUMBER,
            REQUEST.COURIER_COMPANY,
            jsonObject(
                key("id").value(SERVICE.ID),
                key("name").value(SERVICE.NAME)
            ).`as`("service"),
            jsonObject(
                key("id").value(USER.ID),
                key("name").value(USER.NAME),
                key("role").value(USER.ROLE),
                key("branch_serial").value(USER.BRANCH_SERIAL)
            ).`as`("user"),
            jsonObject(
                key("id").value(REQUEST_GROUP.ID)
            ).`as`("request_group"),
            jsonObject(
                key("id").value(REQUEST_RELATION.ID),
                key("name").value(REQUEST_RELATION.NAME)
            ).`as`("request_relation"),
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
                                key("nursing_number").value(ORGANIZATION.NURSING_NUMBER)
                            )
                        )
                    )
                ),
                key("extensions").value(
                    select(
                        jsonArrayAgg(jsonObject(
                            key("id").value(EXTENSION.ID),
                            key("name").value(EXTENSION.NAME),
                            key("value").value(SAMPLE_EXTENSION.VALUE),
                            key("regex").value(EXTENSION.REGEX),
                            key("type").value(EXTENSION.TYPE),
                            key("required").value(SERVICE_EXTENSION.REQUIRED),
                            key("sort_extension").value(SERVICE_EXTENSION.SORT_EXTENSION)
                        ))
                    ).from(EXTENSION)
                        .join(SERVICE_EXTENSION).on(SERVICE_EXTENSION.EXTENSION_ID.eq(EXTENSION.ID))
                        .leftJoin(SAMPLE_EXTENSION)
                        .on(SAMPLE_EXTENSION.EXTENSION_ID.eq(EXTENSION.ID))
                        .and(SAMPLE_EXTENSION.SAMPLE_ID.eq(SAMPLE.ID))
                        .where(SERVICE_EXTENSION.SERVICE_ID.eq(SERVICE.ID))
                )
            ).`as`("sample")
        )

        val condition = if (userDto.role == "USER") {
            REQUEST.USER_ID.eq(userDto.id)
        } else noCondition()

        return selectPage(mainTable = REQUEST, query = query,
            joinTables = joins, selectFields = fields, where = condition) { record ->
            record.into(RequestDTO::class.java)
        }
    }

    fun DSLContext.updateRequestStatusAndCreateAtById(sampleId: UUID, serviceId: String):Mono<RequestDTO> {
        return Mono.from(
            update(REQUEST)
                .set(REQUEST.CREATE_AT, LocalDateTime.now())
                .set(REQUEST.STATUS, Status.UNCONFIRMED_ORDER.toString())
                .where(REQUEST.SERVICE_ID.eq(serviceId)
                    .and(REQUEST.SAMPLE_ID.eq(sampleId))
                )
                .returning()
        ).map { it.into(RequestDTO::class.java) }

    }
    fun DSLContext.updateRequest(request: RequestDTO): Mono<RequestDTO> {
        return Mono.from(
            update(REQUEST)
                .set(REQUEST.USER_SERVICE_ID, coalesce(`val`(request.userServiceId), REQUEST.USER_SERVICE_ID))
                .set(REQUEST.STATUS, coalesce(`val`(request.status), REQUEST.STATUS))
                .set(REQUEST.MEMO, coalesce(`val`(request.memo), REQUEST.MEMO))
                .set(REQUEST.DEPARTMENT, coalesce(`val`(request.department), REQUEST.DEPARTMENT))
                .set(REQUEST.WARD, coalesce(`val`(request.ward), REQUEST.WARD))
                .set(REQUEST.PHYSICIAN, coalesce(`val`(request.physician), REQUEST.PHYSICIAN))
                .set(REQUEST.REQUEST_GROUP_ID, coalesce(`val`(request.requestGroup?.id), REQUEST.REQUEST_GROUP_ID))
                .set(REQUEST.REQUEST_RELATION_ID, coalesce(`val`(request.requestRelation?.id), REQUEST.REQUEST_RELATION_ID))
                .where(
                    REQUEST.SAMPLE_ID.eq(request.sample!!.id),
                    REQUEST.SERVICE_ID.eq(request.service!!.id)
                ).returning()
        ).map { it.into(RequestDTO::class.java) }
    }
    fun DSLContext.deleteRequest(request: RequestDTO): Mono<RequestDTO> {
        return Mono.from(
            deleteFrom(REQUEST).where(
                REQUEST.SAMPLE_ID.eq(request.sample!!.id),
                REQUEST.SERVICE_ID.eq(request.service!!.id)
            ).returning()
        ).map { it.into(RequestDTO::class.java) }
    }

    fun DSLContext.selectRequestById(sampleId: UUID, serviceId: String): Mono<RequestDTO> {
        return Mono.from(
            select(
                REQUEST.USER_SERVICE_ID,
                REQUEST.MEMO,
                REQUEST.DEPARTMENT,
                REQUEST.WARD,
                REQUEST.PHYSICIAN,
                REQUEST.CART_AT,
                jsonObject(
                    key("id").value(USER.ID),
                    key("name").value(USER.NAME)
                ).`as`("user"),
                jsonObject(
                    key("id").value(SERVICE.ID),
                    key("name").value(SERVICE.NAME)
                ).`as`("service"),
                jsonObject(
                    key("id").value(SAMPLE.ID),
                    key("user_sample_id").value(SAMPLE.USER_SAMPLE_ID),
                    key("quantity").value(SAMPLE.QUANTITY),
                    key("age").value(SAMPLE.AGE),
                    key("sampling_on").value(SAMPLE.SAMPLING_ON),
                    key("create_at").value(SAMPLE.CREATE_AT),
                    key("sample_type").value(jsonObject(
                        key("id").value(SAMPLE_TYPE.ID),
                        key("name").value(SAMPLE_TYPE.NAME)
                    )),
                    key("patient").value(jsonObject(
                        key("serial").value(PATIENT.SERIAL),
                        key("name").value(PATIENT.NAME),
                        key("sex").value(PATIENT.SEX),
                        key("birth_year").value(PATIENT.BIRTH_YEAR),
                        key("birth_month").value(PATIENT.BIRTH_MONTH),
                        key("birth_day").value(PATIENT.BIRTH_DAY),
                        key("organization").value(jsonObject(
                            key("id").value(ORGANIZATION.ID),
                            key("name").value(ORGANIZATION.NAME),
                            key("registration_number").value(ORGANIZATION.REGISTRATION_NUMBER),
                            key("type").value(ORGANIZATION.TYPE),
                            key("nursing_number").value(ORGANIZATION.NURSING_NUMBER)
                        ))
                    )),
                    key("extensions").value(
                        select(
                            jsonArrayAgg(jsonObject(
                                    key("id").value(EXTENSION.ID),
                                    key("name").value(EXTENSION.NAME),
                                    key("value").value(SAMPLE_EXTENSION.VALUE),
                                    key("regex").value(EXTENSION.REGEX),
                                    key("type").value(EXTENSION.TYPE),
                                    key("required").value(SERVICE_EXTENSION.REQUIRED),
                                    key("sort_extension").value(SERVICE_EXTENSION.SORT_EXTENSION)
                                ))
                        ).from(EXTENSION)
                            .join(SERVICE_EXTENSION).on(SERVICE_EXTENSION.EXTENSION_ID.eq(EXTENSION.ID))
                            .leftJoin(SAMPLE_EXTENSION)
                            .on(SAMPLE_EXTENSION.EXTENSION_ID.eq(EXTENSION.ID))
                            .and(SAMPLE_EXTENSION.SAMPLE_ID.eq(sampleId))
                            .where(SERVICE_EXTENSION.SERVICE_ID.eq(serviceId))
                    )
                ).`as`("sample")
            ).from(REQUEST)
                .join(USER).on(REQUEST.USER_ID.eq(USER.ID))
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
                .where(
                    REQUEST.SERVICE_ID.eq(serviceId)
                    .and(REQUEST.SAMPLE_ID.eq(sampleId))
                )

        ).map{it.into(RequestDTO::class.java)}
    }
}