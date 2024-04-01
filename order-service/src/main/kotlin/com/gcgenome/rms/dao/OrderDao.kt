package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Order
import com.gcgenome.rms.tables.references.*
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import reactor.core.publisher.Mono
import java.util.*


interface OrderDao {

    fun DSLContext.selectRequestBySampleId(orderId: UUID, sampleId: UUID, serviceId: String): Mono<Order> =
        Mono.from(
            select(
                ORDER.SERIAL,
                ORDER.CREATE_AT,
                ORDER.USER_ID,
                jsonArrayAgg(
                    jsonObject(
                        key("service").value(
                            select(
                                jsonObject(
                                    key("id").value(SERVICE.ID),
                                    key("name").value(SERVICE.NAME),
                                    key("categoryId").value(SERVICE.CATEGORY_ID)
                                )
                            ).from(SERVICE)
                                .where(SERVICE.ID.eq(REQUEST.SERVICE_ID))
                        ),
                        key("user_service_id").value(REQUEST.USER_SERVICE_ID),
                        key("status").value(REQUEST.STATUS),
                        key("memo").value(REQUEST.MEMO),
                        key("department").value(REQUEST.DEPARTMENT),
                        key("ward").value(REQUEST.WARD),
                        key("physician").value(REQUEST.PHYSICIAN),
                        key("create_at").value(REQUEST.CREATE_AT),
                        key("cart_at").value(REQUEST.CART_AT),
                        key("specified_at").value(REQUEST.SPECIFIED_AT),
                        key("complete_at").value(REQUEST.COMPLETE_AT),
                        key("resample_at").value(REQUEST.RESAMPLE_AT),
                        key("last_modify_at").value(REQUEST.LAST_MODIFY_AT),
                        key("emp_id").value(REQUEST.EMP_ID),
                        key("emp_name").value(REQUEST.EMP_NAME),
                        key("emp_mobile").value(REQUEST.EMP_MOBILE),
                        key("test").value(REQUEST.TEST),
                        key("credit").value(REQUEST.CREDIT),
                        key("price").value(REQUEST.PRICE),
                        key("outsourcing_cost").value(REQUEST.OUTSOURCING_COST),
                        key("patient").value(
                            jsonObject(
                                key("serial").value(PATIENT.SERIAL),
                                key("sex").value(PATIENT.SEX),
                                key("name").value(PATIENT.NAME),
                                key("birth_year").value(PATIENT.BIRTH_YEAR),
                                key("birth_month").value(PATIENT.BIRTH_MONTH),
                                key("birth_day").value(PATIENT.BIRTH_DAY),
                                key("organization").value(
                                    select(
                                        jsonObject(
                                            key("id").value(ORGANIZATION.ID),
                                            key("name").value(ORGANIZATION.NAME),
                                            key("type").value(ORGANIZATION.TYPE),
                                            key("registration_number").value(ORGANIZATION.REGISTRATION_NUMBER),
                                            key("nursing_number").value(ORGANIZATION.NURSING_NUMBER)
                                        )
                                    ).from(ORGANIZATION)
                                        .where(
                                            PATIENT.ORGANIZATION_ID.eq(ORGANIZATION.ID)
                                                .and(PATIENT.USER_ID.eq(ORGANIZATION.USER_ID))
                                                .and(ORGANIZATION.USER_ID.ne(ORGANIZATION.ID))
                                        )
                                ),
                                key("samples").value(
                                    select(
                                        jsonArrayAgg(
                                            jsonObject(
                                                key("barcode").value(SAMPLE.BARCODE),
                                                key("user_sample_id").value(SAMPLE.USER_SAMPLE_ID),
                                                key("quantity").value(SAMPLE.QUANTITY),
                                                key("age").value(SAMPLE.AGE),
                                                key("sampling_on").value(SAMPLE.SAMPLING_ON),
                                                key("resample_reason").value(SAMPLE.RESAMPLE_REASON),
                                                key("create_at").value(SAMPLE.CREATE_AT),
                                                key("sample_type_id").value(SAMPLE.SAMPLE_TYPE_ID),
                                                key("patient_serial").value(SAMPLE.PATIENT_SERIAL),
                                                key("extensions").value(
                                                    select(
                                                        jsonArrayAgg(
                                                            jsonObject(
                                                                key("id").value(SAMPLE_EXTENSION.EXTENSION_ID),
                                                                key("value").value(SAMPLE_EXTENSION.VALUE)
                                                            )
                                                        )
                                                    ).from(SAMPLE_EXTENSION)
                                                        .where(SAMPLE.ID.eq(SAMPLE_EXTENSION.SAMPLE_ID))
                                                )
                                            )
                                        )
                                    ).from(SAMPLE).where(REQUEST.SAMPLE_ID.eq(SAMPLE.ID))
                                )
                            )
                        )
                    )
                ).`as`("requests")
            ).from(ORDER)
                .join(REQUEST).on(ORDER.ID.eq(REQUEST.ORDER_ID))
                .join(SAMPLE).on(REQUEST.SAMPLE_ID.eq(SAMPLE.ID))
                .join(PATIENT).on(
                    SAMPLE.PATIENT_SERIAL.eq(PATIENT.SERIAL)
                        .and(
                            SAMPLE.ORGANIZATION_ID.eq(PATIENT.ORGANIZATION_ID)
                                .and(SAMPLE.USER_ID.eq(PATIENT.USER_ID))
                        )
                )
                .where(REQUEST.ORDER_ID.eq(orderId).and(REQUEST.SAMPLE_ID.eq(sampleId)))
                .groupBy(ORDER.SERIAL, ORDER.CREATE_AT, ORDER.USER_ID)
        ).map(Order::toModel)
}