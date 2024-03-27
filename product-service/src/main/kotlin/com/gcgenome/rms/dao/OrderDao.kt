package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Order
import com.gcgenome.rms.tables.references.*
import org.jooq.DSLContext
import org.jooq.impl.DSL
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*


interface OrderDao {

    fun DSLContext.insertOrder(userId: String, serial: String?, createAt: LocalDateTime?): Mono<Order> {
        return Mono.from(
            insertInto(ORDER)
                .set(ORDER.ID, UUID.randomUUID())
                .set(ORDER.USER_ID, userId)
                .set(ORDER.SERIAL, serial)
                .set(ORDER.CREATE_AT, createAt)
                .returning()
        ).map { it.into(Order::class.java) }
    }

    fun DSLContext.selectOrderSerial(orderSerial: String): Mono<String?> {
        return Mono.from(select(ORDER.SERIAL)
            .from(ORDER)
            .where(ORDER.SERIAL.like("$orderSerial%"))
            .orderBy(ORDER.SERIAL.desc())
            .limit(1))
            .mapNotNull { result ->
                result?.component1()
            }
    }

    fun DSLContext.selectOrderById(orderId: UUID): Mono<Order> =
        Mono.from(
            select(
                ORDER.SERIAL,
                ORDER.CREATE_AT,
                ORDER.USER_ID,
                DSL.jsonArrayAgg(
                    DSL.jsonObject(
                        DSL.key("service").value(REQUEST.SERVICE_ID),
                        DSL.key("user_service_id").value(REQUEST.USER_SERVICE_ID),
                        DSL.key("status").value(REQUEST.STATUS),
                        DSL.key("memo").value(REQUEST.MEMO),
                        DSL.key("department").value(REQUEST.DEPARTMENT),
                        DSL.key("ward").value(REQUEST.WARD),
                        DSL.key("physician").value(REQUEST.PHYSICIAN),
                        DSL.key("create_at").value(REQUEST.CREATE_AT),
                        DSL.key("cart_at").value(REQUEST.CART_AT),
                        DSL.key("specified_at").value(REQUEST.SPECIFIED_AT),
                        DSL.key("complete_at").value(REQUEST.COMPLETE_AT),
                        DSL.key("resample_at").value(REQUEST.RESAMPLE_AT),
                        DSL.key("last_modify_at").value(REQUEST.LAST_MODIFY_AT),
                        DSL.key("emp_id").value(REQUEST.EMP_ID),
                        DSL.key("emp_name").value(REQUEST.EMP_NAME),
                        DSL.key("emp_mobile").value(REQUEST.EMP_MOBILE),
                        DSL.key("test").value(REQUEST.TEST),
                        DSL.key("credit").value(REQUEST.CREDIT),
                        DSL.key("price").value(REQUEST.PRICE),
                        DSL.key("outsourcing_cost").value(REQUEST.OUTSOURCING_COST),
                        DSL.key("patient").value(
                            DSL.jsonObject(
                                DSL.key("serial").value(PATIENT.SERIAL),
                                DSL.key("sex").value(PATIENT.SEX),
                                DSL.key("name").value(PATIENT.NAME),
                                DSL.key("birth_year").value(PATIENT.BIRTH_YEAR),
                                DSL.key("birth_month").value(PATIENT.BIRTH_MONTH),
                                DSL.key("birth_day").value(PATIENT.BIRTH_DAY),
                                DSL.key("organization").value(
                                    select(
                                        DSL.jsonObject(
                                            DSL.key("id").value(ORGANIZATION.ID),
                                            DSL.key("name").value(ORGANIZATION.NAME),
                                            DSL.key("type").value(ORGANIZATION.TYPE),
                                            DSL.key("registration_number").value(ORGANIZATION.REGISTRATION_NUMBER),
                                            DSL.key("nursing_number").value(ORGANIZATION.NURSING_NUMBER)
                                        )
                                    ).from(ORGANIZATION)
                                        .where(
                                            PATIENT.ORGANIZATION_ID.eq(ORGANIZATION.ID)
                                                .and(PATIENT.USER_ID.eq(ORGANIZATION.USER_ID))
                                                .and(ORGANIZATION.USER_ID.ne(ORGANIZATION.ID))
                                        )
                                ),
                                DSL.key("sample").value(
                                    select(
                                        DSL.jsonObject(
                                            DSL.key("barcode").value(SAMPLE.BARCODE),
                                            DSL.key("user_sample_id").value(SAMPLE.USER_SAMPLE_ID),
                                            DSL.key("quantity").value(SAMPLE.QUANTITY),
                                            DSL.key("age").value(SAMPLE.AGE),
                                            DSL.key("sampling_on").value(SAMPLE.SAMPLING_ON),
                                            DSL.key("resample_reason").value(SAMPLE.RESAMPLE_REASON),
                                            DSL.key("create_at").value(SAMPLE.CREATE_AT),
                                            DSL.key("sample_type_id").value(SAMPLE.SAMPLE_TYPE_ID),
                                            DSL.key("patient_serial").value(SAMPLE.PATIENT_SERIAL),
                                            DSL.key("extensions").value(
                                                select(
                                                    DSL.jsonArrayAgg(
                                                        DSL.jsonObject(
                                                            DSL.key("id").value(SAMPLE_EXTENSION.EXTENSION_ID),
                                                            DSL.key("value").value(SAMPLE_EXTENSION.VALUE)
                                                        )
                                                    )
                                                ).from(SAMPLE_EXTENSION)
                                                    .where(SAMPLE.ID.eq(SAMPLE_EXTENSION.SAMPLE_ID))
                                            )
                                        )
                                    ).from(SAMPLE).where(REQUEST.SAMPLE_ID.eq(SAMPLE.ID))
                                ))))
                ).`as`("requests")
            ).from(ORDER)
                .join(REQUEST).on(ORDER.ID.eq(REQUEST.ORDER_ID))
                .join(SAMPLE).on(REQUEST.SAMPLE_ID.eq(SAMPLE.ID))
                .join(PATIENT).on(SAMPLE.PATIENT_SERIAL.eq(PATIENT.SERIAL)
                    .and(SAMPLE.ORGANIZATION_ID.eq(PATIENT.ORGANIZATION_ID)
                    .and(SAMPLE.USER_ID.eq(PATIENT.USER_ID))))
                .where(REQUEST.ORDER_ID.eq(orderId))
                .groupBy(ORDER.SERIAL, ORDER.CREATE_AT, ORDER.USER_ID)
        ).map(Order::toModel)
}
