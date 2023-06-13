package com.gcgenome.rms.order

import com.gcgenome.lims.tables.references.*
import com.gcgenome.rms.data.*
import com.gcgenome.rms.entity.Order
import com.gcgenome.rms.entity.QItem.item
import com.gcgenome.rms.repo.OrderRepository
import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*


@Repository("com.gcgenome.rms.order.OrderDao")
class OrderDao(
    val orderRepo: OrderRepository,
    val dslContext: DSLContext
) {
    fun insertOrder(trx: Configuration, userId: String, order: Order_) = trx.dsl()
        .insertInto(ORDER)
        .columns(ORDER.ID, ORDER.CREDIT, ORDER.OUTSOURCING_COST, ORDER.PRICE, ORDER.TEST, ORDER.USER_ID)
        .values(UUID.randomUUID(), order.credit, order.outsourcingCost, order.price, order.test, userId)
        .returning()

    fun findOrders(userId: String): Flux<Order_> {
        val query = dslContext.select(
            ORDER.ID,
            ORDER.TEST,
            ORDER.CREDIT,
            ORDER.PRICE,
            ORDER.OUTSOURCING_COST,
            jsonArrayAgg(jsonObject(
                key("id").value(ITEM.ID),
                key("order_at").value(ITEM.ORDER_AT),
                key("service").value(ITEM.SERVICE_ID),
                key("patient").value(jsonObject(
                    key("serial").value(PATIENT.SERIAL),
                    key("sex").value(PATIENT.SEX),
                    key("name").value(PATIENT.NAME),
                    key("birth_year").value(PATIENT.BIRTH_YEAR),
                    key("birth_month").value(PATIENT.BIRTH_MONTH),
                    key("birth_day").value(PATIENT.BIRTH_DAY)
                ))
            )).`as`("items")
        ).from(ORDER)
            .join(ITEM).on(ORDER.ID.eq(ITEM.ORDER_ID))
            .join(PATIENT).on(ITEM.PATIENT_SERIAL.eq(PATIENT.SERIAL)
                .and(ITEM.ORGANIZATION_ID.eq(PATIENT.ORGANIZATION_ID)
                    .and(ITEM.USER_ID.eq(PATIENT.USER_ID)))
            ).groupBy(ORDER.ID)
        return Flux.from(query).map { record ->
            Order_(
                id = record.getValue("id", UUID::class.java),
                test = record.getValue("test", Boolean::class.java),
                credit = record.getValue("credit", Boolean::class.java),
                price = record.getValue("price", Int::class.java),
                outsourcingCost = record.getValue("outsourcing_cost", Int::class.java),
                items = record.getValue("items", Array<Item_>::class.java).toList()
            )
        }
    }
    fun findOrder(ordersId: UUID): Mono<Order> {
        return orderRepo.findOne(item.orderId.eq(ordersId))
    }
    fun deleteOrder(sampleId: UUID, itemId: UUID, orderId: UUID): Mono<CancelOrder_> =
        orderRepo.findById(orderId).flatMap { order -> orderRepo.delete(order)
            .then(Mono.just(CancelOrder_(sampleId, "의뢰 취소 되었습니다.")
            .apply {this.itemId=itemId; this.orderId=orderId}))
        }
}
