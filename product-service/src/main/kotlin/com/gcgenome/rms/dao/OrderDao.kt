package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Order
import com.gcgenome.rms.tables.references.ORDER
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*


interface OrderDao {

    fun DSLContext.insertOrder(userId: String, serial: String?, createAt: LocalDateTime?): Mono<Order> {
        return Mono.from(
            insertInto(ORDER)
                .set(ORDER.ID, UUID.randomUUID())
                .set(ORDER.USER_ID, userId)
                .set(ORDER.SERIAL, serial)
                .set(ORDER.CREATE_AT, createAt)
                .returning()
        ).map { it.into(Order::class.java) }
    }
}
