package com.gcgenome.rms.cart

import com.gcgenome.rms.dao.OrderDao
import com.gcgenome.rms.data.Order
import com.gcgenome.rms.data.Status
import com.gcgenome.rms.exceptions.OrderNotFoundException
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

@Component
class CartHandler( val dslContext: DSLContext ): OrderDao {

    fun getCartInfo(orderId: UUID, sampleId: UUID, serviceId: String): Mono<Order> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                selectOrderById(orderId, sampleId, serviceId, Status.CART)
                .switchIfEmpty(Mono.error(OrderNotFoundException()))
            }
        })
    }
}