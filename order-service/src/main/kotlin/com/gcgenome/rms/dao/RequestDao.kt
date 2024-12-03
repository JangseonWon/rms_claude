package com.gcgenome.rms.dao

import com.gcgenome.rms.authentication.User
import com.gcgenome.rms.data.Page
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.RequestDTO
import com.gcgenome.rms.tables.references.*
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import reactor.core.publisher.Mono

interface RequestDao: QueryDao {
    fun DSLContext.updateRequest(request: RequestDTO): Mono<RequestDTO> {
        return Mono.from(
            update(REQUEST)
                .set(REQUEST.COURIER_COMPANY, coalesce(`val`(request.courierCompany), REQUEST.COURIER_COMPANY))
                .set(REQUEST.AWB_NUMBER, coalesce(`val`(request.awbNumber), REQUEST.AWB_NUMBER))
                .apply {
                    if (request.status != null) { set(REQUEST.STATUS, request.status!!.name) }
                    else { set(REQUEST.STATUS, REQUEST.STATUS) }
                }
                .where(
                    REQUEST.SAMPLE_ID.eq(request.sample!!.id),
                    REQUEST.SERVICE_ID.eq(request.service!!.id)
                )
                .returning()
        ).map { it.into(RequestDTO::class.java) }
    }
    fun DSLContext.selectRequestsWithPage(query: Query, user: User): Mono<Page<RequestDTO>> {
        val joins = listOf(
            QueryDao.JoinInfo(ORDER, REQUEST.ORDER_ID.eq(ORDER.ID), QueryDao.JoinType.LEFT),
            QueryDao.JoinInfo(SERVICE, REQUEST.SERVICE_ID.eq(SERVICE.ID), QueryDao.JoinType.LEFT),
            QueryDao.JoinInfo(USER, ORDER.USER_ID.eq(USER.ID), QueryDao.JoinType.LEFT),
            QueryDao.JoinInfo(SAMPLE, REQUEST.SAMPLE_ID.eq(SAMPLE.ID), QueryDao.JoinType.LEFT),
            QueryDao.JoinInfo(SAMPLE_TYPE, SAMPLE.SAMPLE_TYPE_ID.eq(SAMPLE_TYPE.ID), QueryDao.JoinType.LEFT),
            QueryDao.JoinInfo(
                PATIENT,
                SAMPLE.PATIENT_SERIAL.eq(PATIENT.SERIAL)
                    .and(SAMPLE.ORGANIZATION_ID.eq(PATIENT.ORGANIZATION_ID))
                    .and(SAMPLE.USER_ID.eq(PATIENT.USER_ID))
                , QueryDao.JoinType.LEFT
            ),
            QueryDao.JoinInfo(
                ORGANIZATION,
                PATIENT.ORGANIZATION_ID.eq(ORGANIZATION.ID)
                    .and(PATIENT.USER_ID.eq(ORGANIZATION.USER_ID))
                , QueryDao.JoinType.LEFT
            )
        )
        val fields = listOf(
            REQUEST.USER_SERVICE_ID.`as`("user_service_id"),
            REQUEST.AWB_NUMBER.`as`("awb_number"),
            REQUEST.COURIER_COMPANY.`as`("courier_company"),
            REQUEST.STATUS.`as`("status"),
            REQUEST.PHYSICIAN.`as`("physician"),
            REQUEST.REPORTED_AT.`as`("reported_at"),
            REQUEST.CREATE_AT.`as`("create_at"),
            REQUEST.SPECIFIED_AT.`as`("specified_at"),
            jsonObject(
                key("id").value(SERVICE.ID),
                key("name").value(SERVICE.NAME)
            ).`as`("service"),
            jsonObject(
                key("id").value(ORDER.ID),
                key("user").value(
                    jsonObject(
                        key("id").value(USER.ID),
                        key("name").value(USER.NAME)
                    )
                )
            ).`as`("order"),
            jsonObject(
                key("id").value(SAMPLE.ID),
                key("barcode").value(SAMPLE.BARCODE),
                key("sample_type").value(
                    jsonObject(
                        key("id").value(SAMPLE_TYPE.ID),
                        key("name").value(SAMPLE_TYPE.NAME),
                        key("name_kr").value(SAMPLE_TYPE.NAME_KR)
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
                                key("registration_number").value(ORGANIZATION.REGISTRATION_NUMBER),
                                key("type").value(ORGANIZATION.TYPE),
                                key("nursing_number").value(ORGANIZATION.NURSING_NUMBER)
                            )
                        )
                    )
                )
            ).`as`("sample")
        )
        val condition = if (user.role == "USER") {
            ORDER.USER_ID.eq(user.id)
        } else noCondition()

        val groupByFields = listOf(
            REQUEST.USER_SERVICE_ID, REQUEST.STATUS, REQUEST.PHYSICIAN, REQUEST.REPORTED_AT, REQUEST.CREATE_AT, REQUEST.SPECIFIED_AT,
            REQUEST.AWB_NUMBER, REQUEST.COURIER_COMPANY,
            ORDER.ID,
            SERVICE.ID,
            USER.ID,
            SAMPLE.ID, SAMPLE_TYPE.ID,
            PATIENT.SERIAL, PATIENT.NAME, PATIENT.SEX, PATIENT.BIRTH_YEAR, PATIENT.BIRTH_MONTH, PATIENT.BIRTH_DAY,
            ORGANIZATION.ID, ORGANIZATION.NAME, ORGANIZATION.REGISTRATION_NUMBER, ORGANIZATION.TYPE, ORGANIZATION.NURSING_NUMBER
        )
        return selectPage(mainTable = REQUEST, query = query, joinTables = joins, selectFields = fields, where = condition, groupByFields = groupByFields) { record ->
            record.into(RequestDTO::class.java)
        }
    }
}