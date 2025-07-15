package com.gcgenome.rms.dao

import com.gcgenome.rms.authentication.User
import com.gcgenome.rms.data.*
import com.gcgenome.rms.tables.references.*
import org.jooq.DSLContext
import org.jooq.impl.DSL
import reactor.core.publisher.Mono

interface RequestDao: QueryDao {

    fun DSLContext.selectStatusCount(user: User): Mono<StatusCount> {
        return Mono.from(
            select(
                DSL.count().filterWhere(REQUEST.STATUS.ne(Status.CART.name).and(REQUEST.IS_CANCEL.eq(false))),
                DSL.count().filterWhere(REQUEST.STATUS.eq(Status.UNCONFIRMED_ORDER.name).and(REQUEST.IS_CANCEL.eq(false))),
                DSL.count().filterWhere(REQUEST.STATUS.eq(Status.COMPLETED_ORDER.name).and(REQUEST.IS_CANCEL.eq(false))),
                DSL.count().filterWhere(REQUEST.STATUS.eq(Status.IN_PROGRESS.name).and(REQUEST.IS_CANCEL.eq(false))),
                DSL.count().filterWhere(REQUEST.STATUS.eq(Status.TEST_FAILED.name).and(REQUEST.IS_CANCEL.eq(false))),
                DSL.count().filterWhere(REQUEST.STATUS.eq(Status.DELIVERED.name).and(REQUEST.IS_CANCEL.eq(false))),
                DSL.count().filterWhere(REQUEST.STATUS.eq(Status.COMPLETED.name).and(REQUEST.IS_CANCEL.eq(false)))
            )
                .from(REQUEST)
                .where().apply {
                    when(user.role) {
                        Role.USER.name -> and(REQUEST.USER_ID.eq(user.id))
                    }
                }
        ).map(StatusCount::toModel)
    }

    fun DSLContext.selectRequestsWithPage(query: Query): Mono<Page<RequestDTO>> {
        val joins = listOf(
            QueryDao.JoinInfo(SERVICE, REQUEST.SERVICE_ID.eq(SERVICE.ID), QueryDao.JoinType.LEFT),
            QueryDao.JoinInfo(USER, REQUEST.USER_ID.eq(USER.ID), QueryDao.JoinType.LEFT),
            QueryDao.JoinInfo(SAMPLE, REQUEST.SAMPLE_ID.eq(SAMPLE.ID), QueryDao.JoinType.LEFT),
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
            REQUEST.STATUS.`as`("status"),
            REQUEST.PHYSICIAN.`as`("physician"),
            REQUEST.CREATE_AT.`as`("create_at"),
            REQUEST.CONFIRMED_AT.`as`("confirmed_at"),
            REQUEST.COURIER_COMPANY.`as`("courier_company"),
            REQUEST.AWB_NUMBER.`as`("awb_number"),
            DSL.jsonObject(
                DSL.key("id").value(USER.ID),
                DSL.key("name").value(USER.NAME)
            ).`as`("user"),
            DSL.jsonObject(
                DSL.key("id").value(SERVICE.ID),
                DSL.key("name").value(SERVICE.NAME)
            ).`as`("service"),
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
                )
            ).`as`("sample")
        )
        val groupByFields = listOf(
            REQUEST.USER_SERVICE_ID, REQUEST.STATUS, REQUEST.PHYSICIAN, REQUEST.CREATE_AT,
            REQUEST.CONFIRMED_AT, REQUEST.COURIER_COMPANY, REQUEST.AWB_NUMBER,
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
}