package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.references.ITEM
import com.gcgenome.rms.data.Item
import com.gcgenome.rms.tables.references.SAMPLE
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.row
import reactor.core.publisher.Mono
import java.util.*

interface ItemDao {
    fun DSLContext.deleteItemById(orderId: UUID, serviceId: String): Mono<Item> {
        return Mono.from(
            deleteFrom(ITEM)
                .where(ITEM.ORDER_ID.eq(orderId).and(ITEM.SERVICE_ID.eq(serviceId))
                        .and(row(ITEM.ORDER_ID, ITEM.SERVICE_ID).notIn(
                            select(SAMPLE.ORDER_ID, SAMPLE.SERVICE_ID).from(SAMPLE).where(SAMPLE.ORDER_ID.eq(orderId).and(SAMPLE.SERVICE_ID.eq(serviceId)))
                        ))
                ).returning()
        ).map { it.into(Item::class.java) }
    }
}
