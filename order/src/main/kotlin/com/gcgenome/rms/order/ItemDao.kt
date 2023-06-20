package com.gcgenome.rms.order

import com.gcgenome.lims.tables.references.ITEM
import com.gcgenome.rms.data.Item_
import org.jooq.DSLContext
import org.jooq.impl.DSL
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDateTime
import java.util.*

interface ItemDao {
    fun DSLContext.insertItem(userId: String, orderId: UUID, item:Item_) =
        insertInto(ITEM)
        .columns(ITEM.ID, ITEM.ORDER_AT, ITEM.ORDER_ID, ITEM.ORGANIZATION_ID, ITEM.PATIENT_SERIAL, ITEM.USER_ID, ITEM.SERVICE_ID)
        .values(UUID.randomUUID(), LocalDateTime.now(), orderId, userId, item.patient.serial, userId, item.service)
        .returning()

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

    fun DSLContext.updateItemById(item: Item_) =
        update(ITEM)
            .set(ITEM.ORDER_AT, LocalDateTime.now())
            .set(ITEM.SERVICE_ID, item.service)
    fun DSLContext.countItemInOrder(orderId: UUID): Mono<Int> =
        Mono.from( select(DSL.count(ITEM.ORDER_ID).`as`("count")).from(ITEM).where(ITEM.ORDER_ID.eq(orderId)) )
            .map { r-> r.getValue("count", Int::class.java)}

    fun DSLContext.countItemInMrn(mrn: String): Mono<Int> =
        Mono.from( select(DSL.count(ITEM.PATIENT_SERIAL).`as`("count")).from(ITEM).where(ITEM.PATIENT_SERIAL.eq(mrn)) )
            .map { r-> r.getValue("count", Int::class.java)}

    fun DSLContext.deleteItem(itemId: UUID) =
        deleteFrom(ITEM).where(ITEM.ID.eq(itemId)).toMono()
}
