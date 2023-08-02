package com.gcgenome.rms.service

import com.gcgenome.rms.tables.references.*
import com.gcgenome.rms.data.Request
import org.jooq.Condition
import org.jooq.DSLContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface RequestDao {
    fun selectRequestWithWhere(where: Condition): Flux<Request>
}

class DefaultRequestDao(private val dslContext: DSLContext) : RequestDao, OrganizationDao {
    override fun selectRequestWithWhere(where: Condition): Flux<Request> {
        val organizationMain= ORGANIZATION.`as`("organization_main")
        val organizationSub= ORGANIZATION.`as`("organization_sub")
        val query = dslContext.select(
            SAMPLE.CREATE_AT,
            SAMPLE.GENOME_BARCODE,
            SAMPLE.DEPARTMENT,
            SAMPLE.SAMPLE_TYPE_ID,
            SAMPLE.WARD,
            SAMPLE.SAMPLING,
            SAMPLE.PHYSICIAN,
            SAMPLE.EMP_ID,
            SAMPLE.EMP_NAME,
            SAMPLE.EMP_MOBILE,
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
            organizationSub.ID,
            organizationSub.USER_ID,
            organizationSub.NAME,
            organizationSub.REGISTRATION_NUMBER,
            organizationSub.NURSING_NUMBER,
            organizationSub.BRANCH_ID,
            organizationSub.BRANCH_NAME,
            organizationSub.TYPE,
            organizationMain.NAME
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
            .join(organizationSub).on(
                PATIENT.ORGANIZATION_ID.eq(organizationSub.ID)
                    .and(PATIENT.USER_ID.eq(organizationSub.USER_ID))
            ).join(organizationMain).on(organizationSub.USER_ID.eq(organizationMain.ID))
            .where(where)
            .orderBy(SAMPLE.CREATE_AT.asc())
        return Flux.from(query).flatMap { record ->
            val mainName = record.get(organizationMain.NAME) as String
            val subName = record.get(organizationSub.NAME) as String
            Mono.just(RequestMap(dslContext).mapToRequest(record, mainName, subName))
        }
    }
}