package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Page
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.RequestDTO
import com.gcgenome.rms.tables.references.*
import org.jooq.DSLContext
import org.jooq.impl.DSL
import reactor.core.publisher.Mono
import java.time.LocalDateTime

interface RequestDao : QueryDao{
    fun DSLContext.insertRequest(request: RequestDTO): Mono<RequestDTO> {
        return Mono.from(
            insertInto(REQUEST)
                .set(REQUEST.SERVICE_ID, request.service!!.id)
                .set(REQUEST.SAMPLE_ID, request.sample!!.id)
                .set(REQUEST.USER_SERVICE_ID, request.userServiceId ?: request.service!!.id)
                .set(REQUEST.STATUS, request.status)
                .set(REQUEST.MEMO, request.memo)
                .set(REQUEST.DEPARTMENT, request.department)
                .set(REQUEST.WARD, request.ward)
                .set(REQUEST.PHYSICIAN, request.physician)
                .set(REQUEST.CREATE_AT, request.status.takeIf { it != "CART" }?.let { LocalDateTime.now() })
                .set(REQUEST.CART_AT, request.status.takeIf { it == "CART" }?.let { LocalDateTime.now() })
                .set(REQUEST.USER_ID, request.user!!.id)
                .set(REQUEST.REQUEST_GROUP_ID, request.requestGroup!!.id)
                .set(REQUEST.REQUEST_RELATION_ID, request.requestRelation!!.id)
                .returning()
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
            DSL.jsonObject(
                DSL.key("id").value(REQUEST_GROUP.ID)
            ).`as`("request_group"),
            DSL.jsonObject(
                DSL.key("id").value(SERVICE.ID),
                DSL.key("name").value(SERVICE.NAME)
            ).`as`("service"),
            DSL.jsonObject(
                DSL.key("id").value(USER.ID),
                DSL.key("name").value(USER.NAME),
                DSL.key("branch_name").value(USER.BRANCH_NAME),
                DSL.key("branch_serial").value(USER.BRANCH_SERIAL)
            ).`as`("user"),
            DSL.jsonObject(
                DSL.key("id").value(SAMPLE.ID),
                DSL.key("barcode").value(SAMPLE.BARCODE),
                DSL.key("patient").value(
                    DSL.jsonObject(
                        DSL.key("serial").value(PATIENT.SERIAL),
                        DSL.key("name").value(PATIENT.NAME),
                        DSL.key("sex").value(PATIENT.SEX),
                        DSL.key("birth_year").value(PATIENT.BIRTH_YEAR),
                        DSL.key("birth_month").value(PATIENT.BIRTH_MONTH),
                        DSL.key("birth_day").value(PATIENT.BIRTH_DAY),
                        DSL.key("organization").value(
                            DSL.jsonObject(
                                DSL.key("id").value(ORGANIZATION.ID),
                                DSL.key("name").value(ORGANIZATION.NAME),
                                DSL.key("registration_number").value(ORGANIZATION.REGISTRATION_NUMBER),
                                DSL.key("type").value(ORGANIZATION.TYPE),
                                DSL.key("nursing_number").value(ORGANIZATION.NURSING_NUMBER)
                            )
                        )
                    )
                ),
                DSL.key("extensions").value(
                    DSL.jsonArrayAggDistinct(
                        DSL.jsonbObject(
                            DSL.key("id").value(SAMPLE_EXTENSION.EXTENSION_ID),
                            DSL.key("value").value(SAMPLE_EXTENSION.VALUE)
                        )
                    )
                ),
                DSL.key("sample_type").value(
                    DSL.jsonObject(
                        DSL.key("id").value(SAMPLE_TYPE.ID),
                        DSL.key("name").value(SAMPLE_TYPE.NAME)
                    )
                )
            ).`as`("sample"),
            DSL.`when`(
                DSL.count(REPORT.ID).greaterThan(0),
                DSL.jsonArrayAggDistinct(
                    DSL.jsonbObject(
                        DSL.key("id").value(REPORT.ID),
                        DSL.key("value").value(REPORT.VALUE),
                        DSL.key("type").value(REPORT.TYPE),
                        DSL.key("create_at").value(REPORT.CREATE_AT)
                    )
                )
            ).`as`("reports")
        )
        val groupByFields = listOf(
            REQUEST.USER_SERVICE_ID, REQUEST.STATUS, REQUEST.PHYSICIAN,
            REQUEST_GROUP.ID, REQUEST_RELATION.ID,
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