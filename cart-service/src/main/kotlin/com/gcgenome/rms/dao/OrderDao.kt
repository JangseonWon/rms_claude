package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Order
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
    fun DSLContext.updateOrderSerialAndCreatedAtById(orderId: UUID, serial: String): Mono<Order> {
        return Mono.from(
            update(ORDER)
                .set(ORDER.CREATE_AT, LocalDateTime.now())
                .set(ORDER.SERIAL, serial)
                .where(ORDER.ID.eq(orderId))
                .returning()
        ).map { it.into(Order::class.java) }
    }
    fun DSLContext.selectOrderMaxSerialByUserId(userId: String): Mono<String> {
        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val baseSerial = "$userId$currentDate"
        val likePattern = "$baseSerial%"
        val defaultSerial = baseSerial+"0001"

        return Mono.from(
            select(coalesce(
                concat(inline(userId), inline(currentDate), lpad(
                    (max(substring(ORDER.SERIAL, baseSerial.length + 1).cast(Int::class.java)) + 1).cast(String::class.java), 4, '0'
                )),
                inline(defaultSerial)
            ))
                .from(ORDER)
                .where(ORDER.USER_ID.eq(userId)
                    .and(ORDER.SERIAL.like(likePattern)))
        ).map { it.into(String::class.java) }
    }
}