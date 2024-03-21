package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.references.*
import com.gcgenome.rms.data.Order
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*


interface OrderDao{
    fun DSLContext.deleteOrderById(orderId: UUID): Mono<Order> {
        return Mono.from(
            deleteFrom(ORDER)
                .where(ORDER.ID.eq(orderId).and(ORDER.ID.notIn(select(ITEM.ORDER_ID).from(ITEM).where(ITEM.ORDER_ID.eq(orderId)))))
                .returning()
        ).map { it.into(Order::class.java) }
    }
}
