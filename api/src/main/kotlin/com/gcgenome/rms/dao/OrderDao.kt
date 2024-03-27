package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.references.*
import com.gcgenome.rms.data.Order
import org.jooq.DSLContext
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
}
