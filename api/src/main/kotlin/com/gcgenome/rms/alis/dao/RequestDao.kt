package com.gcgenome.rms.alis.dao

import com.gcgenome.rms.alis.data.AlisQuery
import com.gcgenome.rms.alis.data.Body
import com.gcgenome.rms.alis.data.RequestDTO
import com.gcgenome.rms.alis.data.Status
import com.gcgenome.rms.tables.references.*
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalTime

interface RequestDao {
    fun DSLContext.selectBody(alisQuery: AlisQuery): Mono<Body> {
        val baseCondition = REQUEST.CREATE_AT.between(
            alisQuery.search.requestDateFrom.atStartOfDay(),
            alisQuery.search.requestDateTo.atTime(LocalTime.MAX)
        ).and(
            REQUEST.STATUS.notIn(Status.CART.name, Status.UNCONFIRMED_ORDER.name)
        )
        val finalCondition = if (alisQuery.search.userId != null) {
            baseCondition.and(ORGANIZATION.USER_ID.eq(alisQuery.search.userId))
        } else { baseCondition }

        return Flux.from(
            select(
                REQUEST.CREATE_AT.`as`("requestAt"),
                concat(SAMPLE.BARCODE,`val`(":"), REQUEST.SERVICE_ID).`as`("requestId"),
                REQUEST.SERVICE_ID.`as`("serviceId"),
                REQUEST.DEPARTMENT.`as`("department"),
                REQUEST.WARD.`as`("ward"),
                REQUEST.PHYSICIAN.`as`("physician"),
                jsonObject(
                    key("userId").value(ORGANIZATION.USER_ID),
                    key("organization").value(jsonObject(
                        key("organizationId").value(ORGANIZATION.ID),
                        key("organizationName").value(ORGANIZATION.NAME),
                        key("registrationNumber").value(ORGANIZATION.REGISTRATION_NUMBER),
                        key("nursingNumber").value(ORGANIZATION.NURSING_NUMBER)
                    ))
                ).`as`("user"),
                jsonObject(
                    key("sampleId").value(SAMPLE.BARCODE),
                    key("sampleTypeId").value(SAMPLE.SAMPLE_TYPE_ID),
                    key("samplingOn").value(SAMPLE.SAMPLING_ON),
                    key("age").value(SAMPLE.AGE),
                    key("patient").value(jsonObject(
                        key("serial").value(PATIENT.SERIAL),
                        key("patientName").value(PATIENT.NAME),
                        key("sex").value(PATIENT.SEX),
                        key("birth").value(jsonObject(
                            key("year").value(PATIENT.BIRTH_YEAR),
                            key("month").value(PATIENT.BIRTH_MONTH),
                            key("day").value(PATIENT.BIRTH_DAY)
                        ))
                    )),
                    key("extensions").value(jsonArrayAggDistinct(
                        jsonbObject(
                            key("extensionId").value(SAMPLE_EXTENSION.EXTENSION_ID),
                            key("extensionValue").value(SAMPLE_EXTENSION.VALUE)
                        )
                    ))
                ).`as`("sample")
            ).from(REQUEST)
                .leftJoin(SAMPLE).on(REQUEST.SAMPLE_ID.eq(SAMPLE.ID))
                .leftJoin(SAMPLE_EXTENSION).on(SAMPLE.ID.eq(SAMPLE_EXTENSION.SAMPLE_ID))
                .leftJoin(PATIENT).on(
                    SAMPLE.PATIENT_SERIAL.eq(PATIENT.SERIAL)
                        .and(SAMPLE.ORGANIZATION_ID.eq(PATIENT.ORGANIZATION_ID))
                        .and(SAMPLE.USER_ID.eq(PATIENT.USER_ID))
                ).leftJoin(ORGANIZATION).on(
                    PATIENT.ORGANIZATION_ID.eq(ORGANIZATION.ID)
                        .and(PATIENT.USER_ID.eq(ORGANIZATION.USER_ID))
                ).where(finalCondition)
                .groupBy(
                    REQUEST.CREATE_AT,
                    REQUEST.SERVICE_ID,
                    REQUEST.DEPARTMENT,
                    REQUEST.WARD,
                    REQUEST.PHYSICIAN,
                    SAMPLE.CREATE_AT,
                    SAMPLE.BARCODE,
                    SAMPLE.SAMPLE_TYPE_ID,
                    SAMPLE.SAMPLING_ON,
                    SAMPLE.AGE,
                    SAMPLE.USER_SAMPLE_ID,
                    PATIENT.NAME,
                    PATIENT.SERIAL,
                    PATIENT.SEX,
                    PATIENT.BIRTH_YEAR,
                    PATIENT.BIRTH_MONTH,
                    PATIENT.BIRTH_DAY,
                    ORGANIZATION.USER_ID,
                    ORGANIZATION.ID,
                    ORGANIZATION.NAME,
                    ORGANIZATION.REGISTRATION_NUMBER,
                    ORGANIZATION.NURSING_NUMBER
                )
        )
        .map { it.into(RequestDTO::class.java) }
        .collectList().map { requests ->
            Body(requests = requests)
        }
    }
}