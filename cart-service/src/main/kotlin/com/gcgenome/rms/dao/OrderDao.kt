package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Order
import com.gcgenome.rms.data.Status
import com.gcgenome.rms.tables.references.ORDER
import com.gcgenome.rms.tables.references.REQUEST
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

interface OrderDao {
    fun DSLContext.deleteOrderById(orderId: UUID): Mono<Order> {
        return Mono.from(
            deleteFrom(ORDER)
                .where(ORDER.ID.eq(orderId)
                    .andNotExists(
                        selectOne().from(REQUEST).where(REQUEST.ORDER_ID.eq(orderId))
                    )
                ).returning()
        ).map { it.into(Order::class.java) }
    }
    fun DSLContext.updateOrderSerialAndCreatedAtById(orderId: UUID, userId: String): Mono<Order> {
        val ofPattern = DateTimeFormatter.ofPattern("yyyyMMdd")
        val currentDate = LocalDate.now().format(ofPattern)
        val serialPrefix = userId + currentDate
        val defaultSerial = serialPrefix + "0001"

        return Mono.from(
            update(ORDER)
                .set(ORDER.CREATE_AT, LocalDateTime.now())
                .set(ORDER.SERIAL,
                        select(coalesce(
                            concat(inline(serialPrefix), lpad((right(max(ORDER.SERIAL), 4).cast(Int::class.java).plus(1)).cast(String::class.java), 4, '0'))
                            , defaultSerial
                        )).from(ORDER).where(ORDER.SERIAL.like("$serialPrefix%"))
                )
                .where(ORDER.ID.eq(orderId))
                .returning()
        ).map { it.into(Order::class.java) }
    }
}