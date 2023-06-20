package com.gcgenome.rms.order

import com.gcgenome.lims.tables.records.OrderRecord
import com.gcgenome.lims.tables.references.*
import com.gcgenome.rms.data.Order_
import org.jooq.DSLContext
import org.jooq.JSON
import org.jooq.Record6
import org.jooq.impl.DSL.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.util.*


interface OrderDao{

    fun DSLContext.updateOrderById(order: Order_): Mono<OrderRecord> =
        Mono.from(
            update(ORDER)
                .set(ORDER.CREDIT, order.credit)
                .set(ORDER.OUTSOURCING_COST, order.outsourcingCost)
                .set(ORDER.PRICE, order.price)
                .set(ORDER.TEST, order.test)
                .where(ORDER.ID.eq(order.id))
                .returning()
        )
    fun DSLContext.insertOrder(userId: String, order: Order_): Mono<OrderRecord> =
        Mono.from(
            insertInto(ORDER)
            .columns(ORDER.ID, ORDER.CREDIT, ORDER.OUTSOURCING_COST, ORDER.PRICE, ORDER.TEST, ORDER.USER_ID)
            .values(UUID.randomUUID(), order.credit, order.outsourcingCost, order.price, order.test, userId)
            .returning()
        )

    fun DSLContext.selectOrderById(orderId: UUID): Mono<Record6<UUID?, Boolean?, Boolean?, Int?, Int?, JSON?>> =
            Mono.from(
                select(
                    ORDER.ID,
                    ORDER.TEST,
                    ORDER.CREDIT,
                    ORDER.PRICE,
                    ORDER.OUTSOURCING_COST,
                    jsonArrayAgg(
                        jsonObject(
                            key("id").value(ITEM.ID),
                            key("order_at").value(ITEM.ORDER_AT),
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
                                                    key("registration_at").value(SAMPLE.REGISTRATION_AT),
                                                    key("serial").value(SAMPLE.SERIAL),
                                                    key("type_id").value(SAMPLE.SAMPLE_TYPE_ID),
                                                    key("age").value(SAMPLE.AGE),
                                                    key("sampling").value(SAMPLE.SAMPLING),
                                                    key("note").value(SAMPLE.NOTE),
                                                    key("extensions").value(
                                                        select(
                                                            jsonArrayAgg(
                                                                jsonObject(
                                                                    key("id").value(SAMPLE_EXTENSION.EXTENSION_ID),
                                                                    key("value").value(SAMPLE_EXTENSION.VALUE.name)
                                                                )
                                                            )
                                                        ).from(SAMPLE_EXTENSION)
                                                            .where(SAMPLE.ID.eq(SAMPLE_EXTENSION.SAMPLE_ID))
                                                    )
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
                    ).where(ORDER.ID.eq(orderId)).groupBy(ORDER.ID)
            )



    fun DSLContext.selectOrders(userId: String): Flux<Record6<UUID?, Boolean?, Boolean?, Int?, Int?, JSON?>> =
        Flux.from(
            select(
                ORDER.ID,
                ORDER.TEST,
                ORDER.CREDIT,
                ORDER.PRICE,
                ORDER.OUTSOURCING_COST,
                jsonArrayAgg(
                    jsonObject(
                        key("id").value(ITEM.ID),
                        key("order_at").value(ITEM.ORDER_AT),
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
                                                key("registration_at").value(SAMPLE.REGISTRATION_AT),
                                                key("serial").value(SAMPLE.SERIAL),
                                                key("type_id").value(SAMPLE.SAMPLE_TYPE_ID),
                                                key("age").value(SAMPLE.AGE),
                                                key("sampling").value(SAMPLE.SAMPLING),
                                                key("note").value(SAMPLE.NOTE),
                                                key("extensions").value(
                                                    select(
                                                        jsonArrayAgg(
                                                            jsonObject(
                                                                key("id").value(SAMPLE_EXTENSION.EXTENSION_ID),
                                                                key("value").value(SAMPLE_EXTENSION.VALUE)
                                                            )
                                                        )
                                                    ).from(SAMPLE_EXTENSION).where(SAMPLE.ID.eq(SAMPLE_EXTENSION.SAMPLE_ID))
                                                )
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
        )

    fun DSLContext.deleteOrder(orderId: UUID) =
        deleteFrom(ORDER).where(ORDER.ID.eq(orderId)).toMono()
}
