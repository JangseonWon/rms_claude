package com.gcgenome.rms.dao

import com.gcgenome.rms.data.RequestDTO
import com.gcgenome.rms.tables.references.*
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import reactor.core.publisher.Mono
import java.util.*

interface RequestDao {
    fun DSLContext.deleteRequest(request: RequestDTO): Mono<RequestDTO> {
        return Mono.from(
            deleteFrom(REQUEST).where(
                REQUEST.ORDER_ID.eq(request.order?.id)
                    .and(REQUEST.SAMPLE_ID.eq(request.sample!!.id))
                    .and(REQUEST.SERVICE_ID.eq(request.service!!.id))
            ).returning()
        ).map { it.into(RequestDTO::class.java) }
    }

    fun DSLContext.selectRequestById(sampleId: UUID, serviceId: String): Mono<RequestDTO> {
        return Mono.from(
            select(
                REQUEST.ORDER_ID,
                REQUEST.USER_SERVICE_ID,
                REQUEST.MEMO,
                REQUEST.DEPARTMENT,
                REQUEST.WARD,
                REQUEST.PHYSICIAN,
                REQUEST.CART_AT,
                jsonObject(
                    key("id").value(SERVICE.ID),
                    key("name").value(SERVICE.NAME)
                ).`as`("service"),
                jsonObject(
                    key("id").value(SAMPLE.ID),
                    key("userSampleId").value(SAMPLE.USER_SAMPLE_ID),
                    key("quantity").value(SAMPLE.QUANTITY),
                    key("age").value(SAMPLE.AGE),
                    key("samplingOn").value(SAMPLE.SAMPLING_ON),
                    key("createAt").value(SAMPLE.CREATE_AT),
                    key("sampleType").value(
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
                        key("birthYear").value(PATIENT.BIRTH_YEAR),
                        key("birthMonth").value(PATIENT.BIRTH_MONTH),
                        key("birthDay").value(PATIENT.BIRTH_DAY),
                        key("organization").value(
                            jsonObject(
                            key("id").value(ORGANIZATION.ID),
                            key("name").value(ORGANIZATION.NAME),
                            key("registrationNumber").value(ORGANIZATION.REGISTRATION_NUMBER),
                            key("type").value(ORGANIZATION.TYPE),
                            key("nursingNumber").value(ORGANIZATION.NURSING_NUMBER),
                            key("user").value(
                                jsonObject(
                                key("id").value(USER.ID)
                            )
                            )
                        )
                        )
                    )
                    ),
                    key("extensions").value(
                        select(
                            jsonArrayAgg(
                                jsonObject(
                                key("id").value(EXTENSION.ID),
                                key("name").value(EXTENSION.NAME),
                                key("value").value(SAMPLE_EXTENSION.VALUE),
                                key("regex").value(EXTENSION.REGEX)
                            )
                            )

                        ).from(SAMPLE_EXTENSION)
                            .join(EXTENSION).on(SAMPLE_EXTENSION.EXTENSION_ID.eq(EXTENSION.ID))
                            .where(SAMPLE_EXTENSION.SAMPLE_ID.eq(sampleId))
                    )
                ).`as`("sample")
            ).from(REQUEST)
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
                .join(USER).on(ORGANIZATION.USER_ID.eq(USER.ID))
                .where(
                    REQUEST.SERVICE_ID.eq(serviceId)
                        .and(REQUEST.SAMPLE_ID.eq(sampleId))
                )

        ).map{it.into(RequestDTO::class.java)}
    }
}