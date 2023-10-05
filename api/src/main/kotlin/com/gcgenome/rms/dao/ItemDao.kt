package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.references.ITEM
import com.gcgenome.rms.data.Item
import com.gcgenome.rms.tables.references.SAMPLE
import org.jooq.DSLContext
import org.jooq.impl.DSL
import reactor.core.publisher.Mono
import java.util.*

interface ItemDao {
    fun DSLContext.insertItem(userId: String, orderId: UUID, organizationId: String, item:Item): Mono<Item> {
        return Mono.from(
            insertInto(ITEM)
                .set(ITEM.ID, UUID.randomUUID())
                .set(ITEM.ORDER_ID, orderId)
                .set(ITEM.ORGANIZATION_ID, organizationId)
                .set(ITEM.PATIENT_SERIAL, item.patient!!.serial)
                .set(ITEM.USER_ID, userId)
                .set(ITEM.SERVICE_ID, item.serviceId)
                .set(ITEM.SERIAL, item.serial)
                .returning()
        ).map { it.into(Item::class.java) }
    }

    fun DSLContext.selectItemById(itemId: UUID): Mono<Item> {
        return Mono.from(selectFrom(ITEM).where(ITEM.ID.eq(itemId))).map { it.into(Item::class.java) }
    }
    fun DSLContext.deleteItemById(itemId: UUID): Mono<Item> {
        return Mono.from(
            deleteFrom(ITEM)
                .where(ITEM.ID.eq(itemId).and(ITEM.ID.notIn(select(SAMPLE.ITEM_ID).from(SAMPLE).where(SAMPLE.ITEM_ID.eq(itemId)))))
                .returning()
        ).map { it.into(Item::class.java) }
    }

}
