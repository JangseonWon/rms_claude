package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.records.ItemRecord
import com.gcgenome.rms.tables.references.ITEM
import com.gcgenome.rms.data.Item
import org.jooq.DSLContext
import org.jooq.impl.DSL
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.util.*

interface ItemDao {
    fun DSLContext.insertItem(userId: String, orderId: UUID, organizationId: String, item:Item): Mono<ItemRecord> {
        return Mono.from(
            insertInto(ITEM)
                .set(ITEM.ID, UUID.randomUUID())
                .set(ITEM.ORDER_ID, orderId)
                .set(ITEM.ORGANIZATION_ID, organizationId)
                .set(ITEM.PATIENT_SERIAL, item.patient.serial)
                .set(ITEM.USER_ID, userId)
                .set(ITEM.SERVICE_ID, item.service)
                .set(ITEM.SERIAL, item.serial)
                .returning()
        )
    }


    fun DSLContext.selectItemById(itemId: UUID) =
        selectFrom(ITEM).where(ITEM.ID.eq(itemId))
    fun DSLContext.findItemValue(itemId: UUID): Mono<Pair<UUID, String>> {
        val query = select(ITEM.ORDER_ID, ITEM.PATIENT_SERIAL).from(ITEM).where(ITEM.ID.eq(itemId))

        return Mono.from(query).map { record ->
            val orderId = record.getValue(ITEM.ORDER_ID, UUID::class.java)
            val mrn = record.getValue(ITEM.PATIENT_SERIAL, String::class.java)
            Pair(orderId, mrn )
        }
    }

    fun DSLContext.updateItemById(itemId: UUID, item: Item) =
        Mono.from(
            update(ITEM)
                .set(ITEM.SERVICE_ID, item.service)
                .where(ITEM.ID.eq(itemId))
        )
    fun DSLContext.countItemInOrder(orderId: UUID): Mono<Int> =
        Mono.from( select(DSL.count(ITEM.ORDER_ID).`as`("count")).from(ITEM).where(ITEM.ORDER_ID.eq(orderId)) )
            .map { r-> r.getValue("count", Int::class.java)}

    fun DSLContext.countItemInMrn(mrn: String): Mono<Int> =
        Mono.from( select(DSL.count(ITEM.PATIENT_SERIAL).`as`("count")).from(ITEM).where(ITEM.PATIENT_SERIAL.eq(mrn)) )
            .map { r-> r.getValue("count", Int::class.java)}

    fun DSLContext.deleteItem(itemId: UUID) =
        deleteFrom(ITEM).where(ITEM.ID.eq(itemId)).toMono()
}
