package com.gcgenome.rms.order

import com.fasterxml.jackson.databind.ObjectMapper
import com.gcgenome.rms.data.Item_
import com.gcgenome.rms.data.Order_
import com.gcgenome.rms.entity.Order
import com.gcgenome.rms.entity.QItem.item
import com.gcgenome.rms.entity.QOrder.order
import com.gcgenome.rms.entity.QPatient.patient
import com.gcgenome.rms.entity.QSample.sample
import com.gcgenome.rms.repo.OrderRepository
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.Expressions
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Repository("com.gcgenome.rms.order.OrderDao")
class OrderDao(
    val orderRepo: OrderRepository
) {
    fun findOrders(userId: String): Flux<Order_> {
        return orderRepo.query {
            it.select(Projections.constructor(
                Order_::class.java,
                order._id.`as`("id"),
                order.test.`as`("test"),
                order.credit.`as`("credit"),
                order.price.`as`("price"),
                order.outsourcingCost.`as`("outsourcingCost"),
                Expressions.stringTemplate("json_agg(json_build_object(" +
                        "'id' , {0}," +
                        "'order_at' , {1}," +
                        "'service' , {2}," +
                        "'patient', json_build_object(" +
                            "'serial', {3}," +
                            "'sex', {4}," +
                            "'birth_year', {5}," +
                            "'birth_month', {6}," +
                            "'birth_day', {7}," +
                            "'samples', (select json_agg(json_build_object(" +
                                    "'id', {8}," +
                                    "'registration_at', {9}" +
                                    ")" +
                                ") from {10} where {11}={12} and {13}={14} and {15}={16}" +
                        ")" +
                        ")" +
                        ")" +
                        ")"
                    , item._id, item.orderAt, item.serviceId, patient.serial, patient.sex, patient.birthYear, patient.birthMonth, patient.birthDay, sample._id, sample.registrationAt, sample, patient.organizationId, sample.organizationId, patient.serial, sample.patientSerial, patient.userId, sample.userId).`as`("item")
            )).from(order).leftJoin(item).on(order._id.eq(item.orderId))
                .fullJoin(patient).on(item.organizationId.eq(patient.organizationId).and(item.patientSerial.eq(patient.serial).and(item.userId.eq(patient.userId))))
                .groupBy(order._id)
                .orderBy(order._id.asc())
        }.all().map(this::map)

    }

    private fun map(dto: Order_) = Order_(
        test = dto.test,
        credit = dto.credit,
        price = dto.price,
        outsourcingCost = dto.outsourcingCost
    ).apply {
        items = ObjectMapper().readValue(dto.item!!, Array<Item_>::class.java).toList()
    }
    fun saveOrder(userId: String, dto: Order_): Mono<Order_> =
        orderRepo.save(map(userId, dto)).map(this::map)

    private fun map(userId: String, dto: Order_) = Order(
        _id = UUID.randomUUID(),
        userId = userId,
        test = dto.test,
        credit = dto.credit,
        price = dto.price,
        outsourcingCost = dto.outsourcingCost
    )
    private fun map(entity: Order) =
        Order_(
            test = entity.test,
            credit = entity.credit,
            price = entity.price,
            outsourcingCost = entity.outsourcingCost
        ).apply {
            id = entity._id
            items = emptyList()
        }
}
