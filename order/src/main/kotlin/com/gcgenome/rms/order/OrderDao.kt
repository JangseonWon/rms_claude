package com.gcgenome.rms.order

import com.gcgenome.lims.tables.references.*
import com.gcgenome.rms.data.Order
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDateTime
import java.util.*


interface OrderDao{

    fun DSLContext.updateOrderById(order: Order) =
        Mono.from(
            update(ORDER)
                .set(ORDER.LAST_MODIFY_AT, LocalDateTime.now())
                .set(ORDER.CREDIT, order.credit)
                .set(ORDER.OUTSOURCING_COST, order.outsourcingCost)
                .set(ORDER.PRICE, order.price)
                .set(ORDER.TEST, order.test)
                .where(ORDER.ID.eq(order.id))
                .returning()
        )
    fun DSLContext.insertOrder(userId: String, order: Order) =
        Mono.from(
            insertInto(ORDER)
            .columns(ORDER.ID, ORDER.CREATE_AT, ORDER.LAST_MODIFY_AT, ORDER.CREDIT, ORDER.OUTSOURCING_COST, ORDER.PRICE, ORDER.TEST, ORDER.USER_ID)
            .values(UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now(), order.credit, order.outsourcingCost, order.price, order.test, userId)
            .returning()
        )

    fun DSLContext.selectOrderById(orderId: UUID) =
        select(
            ORDER.ID,
            ORDER.CREATE_AT,
            ORDER.LAST_MODIFY_AT,
            ORDER.TEST,
            ORDER.CREDIT,
            ORDER.PRICE,
            ORDER.OUTSOURCING_COST,
            jsonArrayAgg(jsonObject(
                key("id").value(ITEM.ID),
                key("service").value(ITEM.SERVICE_ID),
                key("patient").value(jsonObject(
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
                                key("nursing_number").value(ORGANIZATION.NURSING_NUMBER),
                                key("branch_code").value(ORGANIZATION.BRANCH_ID),
                                key("branch_name").value(ORGANIZATION.BRANCH_NAME)
                            )
                        ).from(ORGANIZATION).where(SAMPLE.ORGANIZATION_ID.eq(ORGANIZATION.ID))
                    ),
                    key("samples").value(
                        select(
                            jsonArrayAgg(jsonObject(
                                key("id").value(SAMPLE.ID),
                                key("create_at").value(SAMPLE.CREATE_AT),
                                key("last_modify_at").value(SAMPLE.LAST_MODIFY_AT),
                                key("age").value(SAMPLE.AGE),
                                key("sampling").value(SAMPLE.SAMPLING),
                                key("note").value(SAMPLE.NOTE),
                                key("genome_barcode").value(SAMPLE.GENOME_BARCODE),
                                key("sample_barcode").value(SAMPLE.SAMPLE_BARCODE),
                                key("type").value(SAMPLE.SAMPLE_TYPE_ID),
                                key("department").value(SAMPLE.DEPARTMENT),
                                key("ward").value(SAMPLE.WARD),
                                key("physician").value(SAMPLE.PHYSICIAN),
                                key("extensions").value(
                                    select(
                                        jsonArrayAgg(jsonObject(
                                            key("id").value(SAMPLE_EXTENSION.EXTENSION_ID),
                                            key("value").value(SAMPLE_EXTENSION.VALUE.name)
                                        ))
                                    ).from(SAMPLE_EXTENSION).where(SAMPLE.ID.eq(SAMPLE_EXTENSION.SAMPLE_ID))
                                ),
                                key("state").value(SAMPLE.STATE)
                            ))
                        ).from(SAMPLE).where(ITEM.ID.eq(SAMPLE.ITEM_ID))
                    )
                )),
            )).`as`("items")
        ).from(ORDER)
            .join(ITEM).on(ORDER.ID.eq(ITEM.ORDER_ID))
            .join(PATIENT).on(ITEM.PATIENT_SERIAL.eq(PATIENT.SERIAL)
                .and(ITEM.ORGANIZATION_ID.eq(PATIENT.ORGANIZATION_ID)
                    .and(ITEM.USER_ID.eq(PATIENT.USER_ID))))
            .join(SAMPLE).on(ITEM.ID.eq(SAMPLE.ITEM_ID))
            .where(ORDER.ID.eq(orderId)).groupBy(ORDER.ID)

    fun DSLContext.selectOrderNotINOrganization(orderId: UUID) =
        select(
            ORDER.ID,
            ORDER.CREATE_AT,
            ORDER.LAST_MODIFY_AT,
            ORDER.TEST,
            ORDER.CREDIT,
            ORDER.PRICE,
            ORDER.OUTSOURCING_COST,
            jsonArrayAgg(jsonObject(
                key("id").value(ITEM.ID),
                key("service").value(ITEM.SERVICE_ID),
                key("patient").value(jsonObject(
                    key("serial").value(PATIENT.SERIAL),
                    key("sex").value(PATIENT.SEX),
                    key("name").value(PATIENT.NAME),
                    key("birth_year").value(PATIENT.BIRTH_YEAR),
                    key("birth_month").value(PATIENT.BIRTH_MONTH),
                    key("birth_day").value(PATIENT.BIRTH_DAY),
                    key("samples").value(
                        select(
                            jsonArrayAgg(jsonObject(
                                key("id").value(SAMPLE.ID),
                                key("create_at").value(SAMPLE.CREATE_AT),
                                key("last_modify_at").value(SAMPLE.LAST_MODIFY_AT),
                                key("age").value(SAMPLE.AGE),
                                key("sampling").value(SAMPLE.SAMPLING),
                                key("note").value(SAMPLE.NOTE),
                                key("genome_barcode").value(SAMPLE.GENOME_BARCODE),
                                key("sample_barcode").value(SAMPLE.SAMPLE_BARCODE),
                                key("type").value(SAMPLE.SAMPLE_TYPE_ID),
                                key("department").value(SAMPLE.DEPARTMENT),
                                key("ward").value(SAMPLE.WARD),
                                key("physician").value(SAMPLE.PHYSICIAN),
                                key("extensions").value(
                                    select(
                                        jsonArrayAgg(jsonObject(
                                            key("id").value(SAMPLE_EXTENSION.EXTENSION_ID),
                                            key("value").value(SAMPLE_EXTENSION.VALUE.name)
                                        ))
                                    ).from(SAMPLE_EXTENSION).where(SAMPLE.ID.eq(SAMPLE_EXTENSION.SAMPLE_ID))
                                ),
                                key("state").value(SAMPLE.STATE)
                            ))
                        ).from(SAMPLE).where(ITEM.ID.eq(SAMPLE.ITEM_ID))
                    )
                )),
            )).`as`("items")
        ).from(ORDER)
            .join(ITEM).on(ORDER.ID.eq(ITEM.ORDER_ID))
            .join(PATIENT).on(ITEM.PATIENT_SERIAL.eq(PATIENT.SERIAL)
                .and(ITEM.ORGANIZATION_ID.eq(PATIENT.ORGANIZATION_ID)
                    .and(ITEM.USER_ID.eq(PATIENT.USER_ID)))
            ).where(ORDER.ID.eq(orderId)).groupBy(ORDER.ID)

    fun DSLContext.selectOrders(userId: String): Flux<Order> {
        val query = select(
            ORDER.ID,
            ORDER.CREATE_AT,
            ORDER.LAST_MODIFY_AT,
            ORDER.TEST,
            ORDER.CREDIT,
            ORDER.PRICE,
            ORDER.OUTSOURCING_COST,
            jsonArrayAgg(
                jsonObject(
                    key("id").value(ITEM.ID),
                    key("service").value(ITEM.SERVICE_ID),
                    key("patient").value(
                        jsonObject(
                            key("serial").value(PATIENT.SERIAL),
                            key("sex").value(PATIENT.SEX),
                            key("name").value(PATIENT.NAME),
                            key("birth_year").value(PATIENT.BIRTH_YEAR),
                            key("birth_month").value(PATIENT.BIRTH_MONTH),
                            key("birth_day").value(PATIENT.BIRTH_DAY),
                            key("samples").value(
                                select(
                                    jsonArrayAgg(
                                        jsonObject(
                                            key("id").value(SAMPLE.ID),
                                            key("create_at").value(SAMPLE.CREATE_AT),
                                            key("last_modify_at").value(SAMPLE.LAST_MODIFY_AT),
                                            key("age").value(SAMPLE.AGE),
                                            key("sampling").value(SAMPLE.SAMPLING),
                                            key("note").value(SAMPLE.NOTE),
                                            key("genome_barcode").value(SAMPLE.GENOME_BARCODE),
                                            key("sample_barcode").value(SAMPLE.SAMPLE_BARCODE),
                                            key("type").value(SAMPLE.SAMPLE_TYPE_ID),
                                            key("department").value(SAMPLE.DEPARTMENT),
                                            key("ward").value(SAMPLE.WARD),
                                            key("physician").value(SAMPLE.PHYSICIAN),
                                            key("extensions").value(
                                                select(
                                                    jsonArrayAgg(
                                                        jsonObject(
                                                            key("id").value(SAMPLE_EXTENSION.EXTENSION_ID),
                                                            key("value").value(SAMPLE_EXTENSION.VALUE)
                                                        )
                                                    )
                                                ).from(SAMPLE_EXTENSION).where(SAMPLE.ID.eq(SAMPLE_EXTENSION.SAMPLE_ID))
                                            ),
                                            key("state").value(SAMPLE.STATE)
                                        )
                                    )
                                ).from(SAMPLE).where(ITEM.ID.eq(SAMPLE.ITEM_ID))
                            )
                        )
                    ),
                )
            ).`as`("items")
        ).from(ORDER)
            .join(ITEM).on(ORDER.ID.eq(ITEM.ORDER_ID))
            .join(PATIENT).on(
                ITEM.PATIENT_SERIAL.eq(PATIENT.SERIAL)
                    .and(
                        ITEM.ORGANIZATION_ID.eq(PATIENT.ORGANIZATION_ID)
                            .and(ITEM.USER_ID.eq(PATIENT.USER_ID))
                    )
            ).groupBy(ORDER.ID).orderBy(ORDER.ID)

        return Flux.from(query).map(Order::toModel)
    }
    fun DSLContext.deleteOrder(orderId: UUID) =
        deleteFrom(ORDER).where(ORDER.ID.eq(orderId)).toMono()
}
