package com.gcgenome.rms.dao

import com.gcgenome.rms.data.*
import com.gcgenome.rms.tables.references.*
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import reactor.core.publisher.Mono
import java.time.LocalDateTime

interface RequestDao: QueryDao{

    fun DSLContext.selectRequestsWithPage(query: Query): Mono<Page<RequestDTO>> {
        val joins = listOf(
            QueryDao.JoinInfo(ORDER, REQUEST.ORDER_ID.eq(ORDER.ID), QueryDao.JoinType.LEFT),
            QueryDao.JoinInfo(SERVICE, REQUEST.SERVICE_ID.eq(SERVICE.ID), QueryDao.JoinType.LEFT),
            QueryDao.JoinInfo(USER, ORDER.USER_ID.eq(USER.ID), QueryDao.JoinType.LEFT),
            QueryDao.JoinInfo(SAMPLE, REQUEST.SAMPLE_ID.eq(SAMPLE.ID), QueryDao.JoinType.LEFT),
            QueryDao.JoinInfo(PATIENT,
                SAMPLE.PATIENT_SERIAL.eq(PATIENT.SERIAL)
                    .and(SAMPLE.ORGANIZATION_ID.eq(PATIENT.ORGANIZATION_ID))
                    .and(SAMPLE.USER_ID.eq(PATIENT.USER_ID))
                , QueryDao.JoinType.LEFT
            ),
            QueryDao.JoinInfo(REPORT,
                REQUEST.ORDER_ID.eq(REPORT.ORDER_ID)
                    .and(REQUEST.SERVICE_ID.eq(REPORT.SERVICE_ID))
                    .and(REQUEST.SAMPLE_ID.eq(REPORT.SAMPLE_ID))
                , QueryDao.JoinType.LEFT
            ),
            QueryDao.JoinInfo(ORGANIZATION,
                PATIENT.ORGANIZATION_ID.eq(ORGANIZATION.ID)
                    .and(PATIENT.USER_ID.eq(ORGANIZATION.USER_ID))
                , QueryDao.JoinType.LEFT
            )
        )
        val fields = listOf(
            REQUEST.USER_SERVICE_ID.`as`("user_service_id"),
            REQUEST.STATUS.`as`("status"),
            REQUEST.PHYSICIAN.`as`("physician"),
            REQUEST.REPORTED_AT.`as`("reported_at"),
            jsonObject(
                key("id").value(SERVICE.ID),
                key("name").value(SERVICE.NAME)
            ).`as`("service"),
            jsonObject(
                key("id").value(ORDER.ID),
                key("serial").value(ORDER.SERIAL),
                key("create_at").value(ORDER.CREATE_AT),
                key("user").value(jsonObject(
                    key("id").value(USER.ID)
                ))
            ).`as`("order"),
            jsonObject(
                key("id").value(SAMPLE.ID),
                key("barcode").value(SAMPLE.BARCODE),
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
                ))
            ).`as`("sample"),
            `when`(count(REPORT.ID).greaterThan(0),
                jsonArrayAggDistinct(
                    jsonbObject(
                        key("id").value(REPORT.ID),
                        key("value").value(REPORT.VALUE),
                        key("type").value(REPORT.TYPE),
                        key("create_at").value(REPORT.CREATE_AT)
                    )
                )
            ).`as`("reports")
        )
        val groupByFields = listOf(
            REQUEST.USER_SERVICE_ID, REQUEST.STATUS, REQUEST.PHYSICIAN, REQUEST.REPORTED_AT,
            ORDER.ID,
            SERVICE.ID,
            USER.ID,
            SAMPLE.ID,
            PATIENT.SERIAL, PATIENT.NAME, PATIENT.SEX, PATIENT.BIRTH_YEAR, PATIENT.BIRTH_MONTH, PATIENT.BIRTH_DAY,
            ORGANIZATION.ID, ORGANIZATION.NAME, ORGANIZATION.REGISTRATION_NUMBER, ORGANIZATION.TYPE, ORGANIZATION.NURSING_NUMBER
        )
        return selectPage(mainTable = REQUEST, query = query, joinTables = joins, selectFields = fields, groupByFields = groupByFields) { record ->
            record.into(RequestDTO::class.java)
        }
    }
    fun DSLContext.updateRequestStatusFinish(request: RequestDTO): Mono<RequestDTO> {
        return Mono.from(
            update(REQUEST)
                .set(REQUEST.STATUS, Status.FINISHED.toString())
                .set(REQUEST.COMPLETE_AT, LocalDateTime.now())
                .where(
                    REQUEST.ORDER_ID.eq(request.order!!.id)
                        .and(REQUEST.SERVICE_ID.eq(request.service!!.id))
                        .and(REQUEST.SAMPLE_ID.eq(request.sample!!.id))
                )
                .returning()
        ).map { it.into(RequestDTO::class.java) }
    }
}