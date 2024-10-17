package com.gcgenome.rms.dao

import com.gcgenome.rms.data.OrderDTO
import com.gcgenome.rms.data.Status
import com.gcgenome.rms.tables.references.ORDER
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


interface OrderDao {
    fun DSLContext.insertOrder(userId: String, status: String): Mono<OrderDTO> {
        val ofPattern = DateTimeFormatter.ofPattern("yyyyMMdd")
        val currentDate = LocalDateTime.now().format(ofPattern)
        val serialPrefix = userId + currentDate
        val defaultSerial = serialPrefix + "0001"

        return Mono.from(
            insertInto(ORDER)
                .set(ORDER.ID, UUID.randomUUID())
                .set(ORDER.USER_ID, userId)
                .apply {
                    if (status != Status.CART.toString()){
                        set(ORDER.SERIAL,
                            select(coalesce(
                                concat(left(max(ORDER.SERIAL), length(max(ORDER.SERIAL)) - 4), lpad((right(max(ORDER.SERIAL), 4).cast(Long::class.java).plus(1)).cast(String::class.java), 4, '0'))
                                , defaultSerial
                            )).from(ORDER).where(ORDER.SERIAL.like("$serialPrefix%"))
                        )
                    }
                }
                .set(ORDER.CREATE_AT, status.takeIf { it != Status.CART.toString() }?.let { LocalDateTime.now() })
                .returning()
        ).map { it.into(OrderDTO::class.java) }
    }
}
