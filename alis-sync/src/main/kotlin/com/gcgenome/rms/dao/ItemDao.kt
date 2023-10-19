package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Item
import com.gcgenome.rms.data.RmsOrder
import com.gcgenome.rms.tables.references.ITEM
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.util.*

interface ItemDao {
    fun DSLContext.insertItem(rmsOrder: RmsOrder): Mono<Item> {
        return Mono.from(
            insertInto(ITEM)
                .set(ITEM.ID, rmsOrder.itemId)
                .set(ITEM.ORDER_ID, rmsOrder.orderId)
                .set(ITEM.ORGANIZATION_ID, rmsOrder.organizationId)
                .set(ITEM.PATIENT_SERIAL, rmsOrder.mrn)
                .set(ITEM.USER_ID, rmsOrder.userId)
                .set(ITEM.SERVICE_ID, rmsOrder.serviceId)
                .set(ITEM.SERIAL, rmsOrder.serial)
                .returning()
        ).map { it.into(Item::class.java) }
    }
}
