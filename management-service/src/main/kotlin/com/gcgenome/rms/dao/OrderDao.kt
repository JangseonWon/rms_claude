package com.gcgenome.rms.dao

import com.gcgenome.rms.data.OrderDTO
import com.gcgenome.rms.tables.references.ORDER
import com.gcgenome.rms.tables.references.REQUEST
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.util.*

interface OrderDao {
    fun DSLContext.deleteOrderById(orderId: UUID): Mono<OrderDTO> {
        return Mono.from(
            deleteFrom(ORDER)
                .where(ORDER.ID.eq(orderId)
                    .andNotExists(
                        selectOne().from(REQUEST).where(REQUEST.ORDER_ID.eq(orderId))
                    )
                ).returning()
        ).map { it.into(OrderDTO::class.java) }
    }
}