package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Item
import com.gcgenome.rms.tables.records.ItemRecord
import com.gcgenome.rms.tables.references.*
import org.jooq.DSLContext
import org.jooq.impl.DSL
import reactor.core.publisher.Mono
import java.util.*

interface ItemDao {
    fun DSLContext.insertItem(orderId: UUID, serviceId: String, serial: String): Mono<ItemRecord> {
        return Mono.from(
            insertInto(ITEM)
                .set(ITEM.ORDER_ID, orderId)
                .set(ITEM.SERVICE_ID, serviceId)
                .set(ITEM.SERIAL, serial)
                .returning()
        )
    }

    fun DSLContext.selectItemById(orderId: UUID, userId: String): Mono<Item> =
        Mono.from(
            select(
                ITEM.SERVICE_ID,
                ITEM.SERIAL,
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
                    DSL.key("samples").value(
                        select(
                            DSL.jsonArrayAgg(
                                DSL.jsonObject(
                                    DSL.key("retest_reason").value(SAMPLE.RETEST_REASON),
                                    DSL.key("status").value(SAMPLE.STATUS),
                                    DSL.key("barcode").value(SAMPLE.BARCODE),
                                    DSL.key("serial").value(SAMPLE.SERIAL),
                                    DSL.key("quantity").value(SAMPLE.QUANTITY),
                                    DSL.key("age").value(SAMPLE.AGE),
                                    DSL.key("memo").value(SAMPLE.MEMO),
                                    DSL.key("department").value(SAMPLE.DEPARTMENT),
                                    DSL.key("ward").value(SAMPLE.WARD),
                                    DSL.key("physician").value(SAMPLE.PHYSICIAN),
                                    DSL.key("sampling").value(SAMPLE.SAMPLING),
                                    DSL.key("create_at").value(SAMPLE.CREATE_AT),
                                    DSL.key("cart_at").value(SAMPLE.CART_AT),
                                    DSL.key("specified_at").value(SAMPLE.SPECIFIED_AT),
                                    DSL.key("complete_at").value(SAMPLE.COMPLETE_AT),
                                    DSL.key("resample_at").value(SAMPLE.RESAMPLE_AT),
                                    DSL.key("last_modify_at").value(SAMPLE.LAST_MODIFY_AT),
                                    DSL.key("emp_id").value(SAMPLE.EMP_ID),
                                    DSL.key("emp_name").value(SAMPLE.EMP_NAME),
                                    DSL.key("emp_mobile").value(SAMPLE.EMP_MOBILE),
                                    DSL.key("labs_test").value(SAMPLE.LABS_TEST),
                                    DSL.key("labs_credit").value(SAMPLE.LABS_CREDIT),
                                    DSL.key("labs_price").value(SAMPLE.LABS_PRICE),
                                    DSL.key("labs_outsourcing_cost").value(SAMPLE.LABS_OUTSOURCING_COST),
                                    DSL.key("extensions").value(
                                        select(
                                            DSL.jsonArray(
                                                DSL.jsonObject(
                                                    DSL.key("id").value(SAMPLE_EXTENSION.EXTENSION_ID),
                                                    DSL.key("value").value(SAMPLE_EXTENSION.VALUE.name)
                                                )
                                            )
                                        ).from(SAMPLE_EXTENSION)
                                            .where(SAMPLE.ID.eq(SAMPLE_EXTENSION.SAMPLE_ID))
                                    ),
                                )
                            )
                        ).from(SAMPLE).where(ITEM.ORDER_ID.eq(SAMPLE.ORDER_ID))
                    )
                ).`as`("patient"),
            ).from(ORDER)
                .join(ITEM).on(ORDER.ID.eq(ITEM.ORDER_ID))
                .join(SAMPLE).on(ITEM.ORDER_ID.eq(SAMPLE.ORDER_ID).and(ITEM.SERVICE_ID.eq(SAMPLE.SERVICE_ID)))
                .join(PATIENT).on(
                    SAMPLE.PATIENT_SERIAL.eq(PATIENT.SERIAL)
                        .and(
                            SAMPLE.ORGANIZATION_ID.eq(PATIENT.ORGANIZATION_ID)
                            .and(SAMPLE.USER_ID.eq(PATIENT.USER_ID)))
                )
                .where(ORDER.ID.eq(orderId).and(ORDER.USER_ID.eq(userId)))
        ).map(Item::toModel)
}
