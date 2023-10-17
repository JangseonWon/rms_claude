package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Order
import com.gcgenome.rms.data.RmsOrder
import com.gcgenome.rms.tables.references.ORDER
import org.jooq.DSLContext
import reactor.core.publisher.Mono


interface OrderDao{

    fun DSLContext.insertOrder(rmsOrder: RmsOrder): Mono<Order> {
        return Mono.from(
            insertInto(ORDER)
                .set(ORDER.ID, rmsOrder.orderId)
                .set(ORDER.CREATE_AT, rmsOrder.createAt)
                .set(ORDER.LAST_MODIFY_AT, rmsOrder.createAt)
                .set(ORDER.TEST, false)
                .set(ORDER.CREDIT, false)
                .set(ORDER.PRICE, rmsOrder.price)
                .set(ORDER.OUTSOURCING_COST, rmsOrder.outsourcingCost)
                .set(ORDER.USER_ID, rmsOrder.userId)
                .returning()
        ).map { it.into(Order::class.java) }
    }

}
