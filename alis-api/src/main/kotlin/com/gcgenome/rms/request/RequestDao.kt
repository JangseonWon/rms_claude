package com.gcgenome.rms.request

import com.gcgenome.lims.tables.references.*
import com.gcgenome.rms.data.Request
import org.jooq.Condition
import org.jooq.DSLContext
import reactor.core.publisher.Flux

interface RequestDao {
    fun selectRequestWithWhere(where: Condition): Flux<Request>
}

class DefaultRequestDao(private val dslContext: DSLContext) : RequestDao {
    override fun selectRequestWithWhere(where: Condition): Flux<Request> {
        val query = dslContext.select(
            SAMPLE.CREATE_AT,
            SAMPLE.GENOME_BARCODE,
            SAMPLE.DEPARTMENT,
            SAMPLE.SAMPLE_TYPE_ID,
            SAMPLE.WARD,
            SAMPLE.SAMPLING,
            SAMPLE.PHYSICIAN,
            SAMPLE_TYPE.NAME,
            PATIENT.NAME,
            PATIENT.SERIAL,
            PATIENT.BIRTH_YEAR,
            PATIENT.BIRTH_MONTH,
            PATIENT.BIRTH_DAY,
            PATIENT.SEX,
            ORDER.TEST,
            ORDER.CREDIT,
            ORDER.PRICE,
            ORDER.OUTSOURCING_COST,
            ITEM.SERVICE_ID,
            ORGANIZATION.ID,
            ORGANIZATION.NAME,
            ORGANIZATION.REGISTRATION_NUMBER,
            ORGANIZATION.NURSING_NUMBER,
            ORGANIZATION.BRANCH_ID,
            ORGANIZATION.BRANCH_NAME,
            ORGANIZATION.TYPE
        )
            .from(SAMPLE)
            .join(SAMPLE_TYPE).on(SAMPLE.SAMPLE_TYPE_ID.eq(SAMPLE_TYPE.ID))
            .join(ITEM).on(SAMPLE.ITEM_ID.eq(ITEM.ID))
            .join(ORDER).on(ORDER.ID.eq(ITEM.ORDER_ID))
            .join(PATIENT).on(
                ITEM.PATIENT_SERIAL.eq(PATIENT.SERIAL)
                    .and(ITEM.ORGANIZATION_ID.eq(PATIENT.ORGANIZATION_ID))
                    .and(ITEM.USER_ID.eq(PATIENT.USER_ID))
            )
            .join(ORGANIZATION).on(
                PATIENT.ORGANIZATION_ID.eq(ORGANIZATION.ID)
                    .and(PATIENT.USER_ID.eq(ORGANIZATION.USER_ID))
            ).where(where)
            .groupBy(
                SAMPLE.CREATE_AT,
                SAMPLE.GENOME_BARCODE,
                SAMPLE.DEPARTMENT,
                SAMPLE.SAMPLE_TYPE_ID,
                SAMPLE.WARD,
                SAMPLE.SAMPLING,
                SAMPLE.PHYSICIAN,
                SAMPLE_TYPE.NAME,
                PATIENT.NAME,
                PATIENT.SERIAL,
                PATIENT.BIRTH_YEAR,
                PATIENT.BIRTH_MONTH,
                PATIENT.BIRTH_DAY,
                PATIENT.SEX,
                ORDER.TEST,
                ORDER.CREDIT,
                ORDER.PRICE,
                ORDER.OUTSOURCING_COST,
                ITEM.SERVICE_ID,
                ORGANIZATION.ID,
                ORGANIZATION.NAME,
                ORGANIZATION.REGISTRATION_NUMBER,
                ORGANIZATION.NURSING_NUMBER,
                ORGANIZATION.BRANCH_ID,
                ORGANIZATION.BRANCH_NAME,
                ORGANIZATION.TYPE
            )
            .orderBy(SAMPLE.CREATE_AT.asc())
        return Flux.from(query).map(Request::mapToRequest)
    }
}