package com.gcgenome.rms.order

import com.gcgenome.rms.data.Order_
import com.gcgenome.rms.entity.Order
import com.gcgenome.rms.repo.OrderRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

@Repository("com.gcgenome.rms.order.OrderDao")
class OrderDao(
    val orderRepo: OrderRepository,
) {
    fun saveOrder(userId: String, dto: Order_): Mono<Order_> =
        orderRepo.save(map(userId, dto)).map(this::map)

    private fun map(userId: String, dto: Order_): Order = Order(
        _id = UUID.randomUUID(),
        userId = userId,
        test = dto.test,
        credit = dto.credit,
        price = dto.price,
        outsourcingCost = dto.outsourcingCost
    )
    private fun map(entity: Order) =
        Order_().apply {
            id = entity._id
        }
}
