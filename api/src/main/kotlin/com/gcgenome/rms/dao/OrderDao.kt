package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.references.*
import com.gcgenome.rms.data.Order
import com.gcgenome.rms.data.SearchCondition
import com.gcgenome.rms.tables.records.OrderRecord
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortOrder
import org.jooq.TableField
import org.jooq.impl.DSL.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*


interface OrderDao{
    fun DSLContext.selectOrderBySerial(serial: String): Mono<Order>{
        return Mono.from(
            selectFrom(ORDER)
                .where(ORDER.SERIAL.eq(serial))
        ).map { it.into(Order::class.java) }
    }

    fun DSLContext.deleteOrderById(orderId: UUID): Mono<Order> {
        return Mono.from(
            deleteFrom(ORDER)
                .where(ORDER.ID.eq(orderId)
                    .and(ORDER.ID.notIn(select(REQUEST.ORDER_ID).from(REQUEST).where(REQUEST.ORDER_ID.eq(orderId)))))
                .returning()
        ).map { it.into(Order::class.java) }
    }

    fun DSLContext.selectOrderByCondition(condition: SearchCondition, whereClause:Condition): Flux<Order> {
        val order: TableField<OrderRecord,out Any?> = when(condition.sort?.field){
            "id" -> ORDER.SERIAL
            else -> ORDER.CREATE_AT
        }

        val asc: SortOrder = when(condition.sort?.asc) {
            true -> SortOrder.ASC
            false -> SortOrder.DESC
            else -> SortOrder.DEFAULT
        }

        return Flux.from(
            select(
                ORDER.SERIAL,
                ORDER.CREATE_AT,
                jsonArrayAgg(jsonObject(
                    key("service").value(jsonObject(
                        key("id").value(SERVICE.ID),
                        key("name").value(SERVICE.NAME))
                    ),
                    key("user_service_id").value(REQUEST.USER_SERVICE_ID),
                    key("status").value(REQUEST.STATUS),
                    key("memo").value(REQUEST.MEMO),
                    key("department").value(REQUEST.DEPARTMENT),
                    key("ward").value(REQUEST.WARD),
                    key("physician").value(REQUEST.PHYSICIAN),
                    key("create_at").value(REQUEST.CREATE_AT),
                    key("specified_at").value(REQUEST.SPECIFIED_AT),
                    key("compete_at").value(REQUEST.COMPLETE_AT),
                    key("resample_at").value(REQUEST.RESAMPLE_AT),
                    key("last_modify_at").value(REQUEST.LAST_MODIFY_AT),
                    key("emp_id").value(REQUEST.EMP_ID),
                    key("emp_name").value(REQUEST.EMP_NAME),
                    key("emp_mobile").value(REQUEST.EMP_MOBILE),
                    key("test").value(REQUEST.TEST),
                    key("credit").value(REQUEST.CREDIT),
                    key("price").value(REQUEST.PRICE),
                    key("outsourcing_cost").value(REQUEST.OUTSOURCING_COST),
                    key("sample").value(jsonObject(
                                key("id").value(SAMPLE.BARCODE),
                                key("user_sample_id").value(SAMPLE.USER_SAMPLE_ID),
                                key("quantity").value(SAMPLE.QUANTITY),
                                key("age").value(SAMPLE.AGE),
                                key("resample_reason").value(SAMPLE.RESAMPLE_REASON),
                                key("sampling_on").value(SAMPLE.SAMPLING_ON),
                                key("create_at").value(SAMPLE.CREATE_AT),
                                key("sample_type_id").value(SAMPLE.SAMPLE_TYPE_ID),
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
                                        key("type").value(ORGANIZATION.TYPE),
                                        key("registration_number").value(ORGANIZATION.REGISTRATION_NUMBER),
                                        key("nursing_number").value(ORGANIZATION.NURSING_NUMBER)
                                    ))
                                )),
                                key("extensions").value(
                                    select(
                                        jsonArrayAgg(jsonObject(
                                            key("id").value(SAMPLE_EXTENSION.EXTENSION_ID),
                                            key("value").value(SAMPLE_EXTENSION.VALUE)
                                        ))
                                    ).from(SAMPLE_EXTENSION).where(SAMPLE.ID.eq(SAMPLE_EXTENSION.SAMPLE_ID))
                                )
                            )
                    ),
                    key("reports").value(
                        select(
                            jsonArrayAgg(jsonObject(
                                key("create_at").value(REPORT.CREATE_AT),
                                key("reported_at").value(REPORT.REPORTED_AT),
                                key("type").value(REPORT.TYPE),
                                key("value").value(REPORT.VALUE),
                                key("is_latest").value(REPORT.IS_LATEST)
                            ))
                        ).from(REPORT)
                            .where(REQUEST.ORDER_ID.eq(REPORT.ORDER_ID)
                                .and(REQUEST.SERVICE_ID.eq(REPORT.SERVICE_ID))
                                .and(REQUEST.SAMPLE_ID.eq(REPORT.SAMPLE_ID)))
                    )
                )).`as`("requests")
            ).from(ORDER)
                .leftOuterJoin(REQUEST).on(REQUEST.ORDER_ID.eq(ORDER.ID))
                .leftOuterJoin(SERVICE).on(SERVICE.ID.eq(REQUEST.SERVICE_ID))
                .leftOuterJoin(SAMPLE).on(SAMPLE.ID.eq(REQUEST.SAMPLE_ID))
                .leftOuterJoin(PATIENT).on(PATIENT.SERIAL.eq(SAMPLE.PATIENT_SERIAL)
                    .and(PATIENT.ORGANIZATION_ID.eq(SAMPLE.ORGANIZATION_ID))
                    .and(PATIENT.USER_ID.eq(SAMPLE.USER_ID)))
                .leftOuterJoin(ORGANIZATION).on(ORGANIZATION.ID.eq(PATIENT.ORGANIZATION_ID)
                        .and(ORGANIZATION.USER_ID.eq(PATIENT.USER_ID)))
                .where(whereClause)
                .groupBy(ORDER.ID)
                .orderBy(order.sort(asc))
        ).map { it.into(Order::class.java) }
    }
}
