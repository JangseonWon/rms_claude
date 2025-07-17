package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Page
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.RequestDTO
import com.gcgenome.rms.tables.references.*
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import reactor.core.publisher.Mono
import java.time.LocalDateTime

interface RequestDao: QueryDao {
    fun DSLContext.insertRequest(request: RequestDTO): Mono<RequestDTO> {
        return Mono.from(
            insertInto(REQUEST)
                .set(REQUEST.SERVICE_ID, request.service!!.id)
                .set(REQUEST.SAMPLE_ID, request.sample!!.id)
                .set(REQUEST.USER_SERVICE_ID, request.userServiceId ?: request.service!!.id)
                .set(REQUEST.STATUS, request.status.toString())
                .set(REQUEST.MEMO, request.memo)
                .set(REQUEST.DEPARTMENT, request.department)
                .set(REQUEST.WARD, request.ward)
                .set(REQUEST.PHYSICIAN, request.physician)
                .set(REQUEST.CREATE_AT, request.createAt)
                .set(REQUEST.USER_ID, request.user!!.id)
                .set(REQUEST.REQUEST_GROUP_ID, request.requestGroup!!.id)
                .set(REQUEST.REQUEST_RELATION_ID, request.requestRelation!!.id)
                .set(REQUEST.IS_CANCEL, false)
                .returning()
        ).map { it.into(RequestDTO::class.java) }
    }

    fun DSLContext.deleteRequestById(request: RequestDTO): Mono<RequestDTO> {
        return Mono.from(
            deleteFrom(REQUEST).where(
                    REQUEST.SAMPLE_ID.eq(request.sample!!.id),
                    REQUEST.SERVICE_ID.eq(request.service!!.id)
            ).returning()
        ).map { it.into(RequestDTO::class.java) }
    }

    fun DSLContext.cancelRequestById(request: RequestDTO): Mono<RequestDTO> {
        return Mono.from(
            update(REQUEST)
                .set(REQUEST.IS_CANCEL, true)
                .set(REQUEST.CANCEL_AT, LocalDateTime.now())
                .where(REQUEST.SAMPLE_ID.eq(request.sample?.id)).returning()
        ).map { it.into(RequestDTO::class.java) }
    }

    fun DSLContext.selectRequestsWithPage(query: Query): Mono<Page<RequestDTO>> {
        val mainTable = REQUEST
        val joins = listOf(
            QueryDao.JoinInfo(REQUEST_GROUP, REQUEST.REQUEST_GROUP_ID.eq(REQUEST_GROUP.ID), QueryDao.JoinType.LEFT),
            QueryDao.JoinInfo(REQUEST_RELATION, REQUEST.REQUEST_RELATION_ID.eq(REQUEST_RELATION.ID), QueryDao.JoinType.LEFT),
            QueryDao.JoinInfo(SERVICE, REQUEST.SERVICE_ID.eq(SERVICE.ID), QueryDao.JoinType.LEFT),
            QueryDao.JoinInfo(USER, REQUEST.USER_ID.eq(USER.ID), QueryDao.JoinType.LEFT),
            QueryDao.JoinInfo(SAMPLE, REQUEST.SAMPLE_ID.eq(SAMPLE.ID), QueryDao.JoinType.LEFT),
            QueryDao.JoinInfo(SAMPLE_TYPE, SAMPLE.SAMPLE_TYPE_ID.eq(SAMPLE_TYPE.ID), QueryDao.JoinType.LEFT),
            QueryDao.JoinInfo(SAMPLE_EXTENSION, SAMPLE.ID.eq(SAMPLE_EXTENSION.SAMPLE_ID), QueryDao.JoinType.LEFT),
            QueryDao.JoinInfo(
                PATIENT,
                SAMPLE.PATIENT_SERIAL.eq(PATIENT.SERIAL)
                    .and(SAMPLE.ORGANIZATION_ID.eq(PATIENT.ORGANIZATION_ID))
                    .and(SAMPLE.USER_ID.eq(PATIENT.USER_ID))
                , QueryDao.JoinType.LEFT
            ),
            QueryDao.JoinInfo(
                REPORT,
                REQUEST.SERVICE_ID.eq(REPORT.SERVICE_ID)
                    .and(REQUEST.SAMPLE_ID.eq(REPORT.SAMPLE_ID))
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
            REQUEST.STATUS.`as`("status"),
            REQUEST.PHYSICIAN.`as`("physician"),
            REQUEST.COURIER_COMPANY.`as`("courier_company"),
            REQUEST.AWB_NUMBER.`as`("awb_number"),
            REQUEST.CREATE_AT.`as`("create_at"),
            REQUEST.IS_CANCEL.`as`("is_cancel"),
            REQUEST.CANCEL_AT.`as`("cancel_at"),
            REQUEST.LIMS_RESAMPLE_AT.`as`("lims_resample_at"),
            REQUEST.LIMS_RESAMPLE_REASON.`as`("lims_resample_reason"),
            jsonObject(
                key("id").value(REQUEST_GROUP.ID)
            ).`as`("request_group"),
            jsonObject(
                key("id").value(SERVICE.ID),
                key("name").value(SERVICE.NAME)
            ).`as`("service"),
            jsonObject(
                key("id").value(USER.ID),
                key("name").value(USER.NAME),
                key("branch_name").value(USER.BRANCH_NAME),
                key("branch_serial").value(USER.BRANCH_SERIAL)
            ).`as`("user"),
            jsonObject(
                key("id").value(SAMPLE.ID),
                key("barcode").value(SAMPLE.BARCODE),
                key("sampling_on").value(SAMPLE.SAMPLING_ON),
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
                ),
                key("extensions").value(
                    jsonArrayAggDistinct(
                        jsonbObject(
                            key("id").value(SAMPLE_EXTENSION.EXTENSION_ID),
                            key("value").value(SAMPLE_EXTENSION.VALUE)
                        )
                    )
                ),
                key("sample_type").value(
                    jsonObject(
                        key("id").value(SAMPLE_TYPE.ID),
                        key("name").value(SAMPLE_TYPE.NAME)
                    )
                )
            ).`as`("sample"),
            `when`(
                count(REPORT.ID).greaterThan(0),
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
            REQUEST.USER_SERVICE_ID, REQUEST.STATUS, REQUEST.PHYSICIAN, REQUEST.CREATE_AT, REQUEST.COURIER_COMPANY, REQUEST.AWB_NUMBER,
            REQUEST_GROUP.ID, REQUEST_RELATION.ID, REQUEST.IS_CANCEL, REQUEST.CANCEL_AT, REQUEST.LIMS_RESAMPLE_AT, REQUEST.LIMS_RESAMPLE_REASON,
            SERVICE.ID,
            USER.ID,
            SAMPLE.ID,
            SAMPLE_TYPE.ID,
            PATIENT.SERIAL, PATIENT.NAME, PATIENT.SEX, PATIENT.BIRTH_YEAR, PATIENT.BIRTH_MONTH, PATIENT.BIRTH_DAY,
            ORGANIZATION.ID, ORGANIZATION.NAME, ORGANIZATION.REGISTRATION_NUMBER, ORGANIZATION.TYPE, ORGANIZATION.NURSING_NUMBER
        )
        return selectPage(mainTable = mainTable, query = query, joinTables = joins, selectFields = fields, groupByFields = groupByFields) { record ->
            record.into(RequestDTO::class.java)
        }
    }
}