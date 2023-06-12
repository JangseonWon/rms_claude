package com.gcgenome.rms.order

import com.gcgenome.lims.tables.references.*
import com.gcgenome.rms.data.CancelOrder_
import com.gcgenome.rms.data.Order_
import com.gcgenome.rms.entity.Order
import com.gcgenome.rms.entity.QItem.item
import com.gcgenome.rms.repo.OrderRepository
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDateTime
import java.util.*


@Repository("com.gcgenome.rms.order.OrderDao")
class OrderDao(
    val orderRepo: OrderRepository,
    val dslContext: DSLContext
) {
    fun saveOrder(userId: String, dto: Order_): Mono<Order_> {
        return Mono.from(dslContext.transactionPublisher{ trx ->
            Flux.from(
                Flux.fromIterable(dto.items).flatMap { item ->
                    trx.dsl()
                        .insertInto(PATIENT)
                        .columns(PATIENT.ORGANIZATION_ID, PATIENT.SERIAL, PATIENT.USER_ID, PATIENT.BIRTH_DAY, PATIENT.BIRTH_MONTH, PATIENT.BIRTH_YEAR, PATIENT.NAME, PATIENT.SEX)
                        .values(userId, item.patient.serial, userId, item.patient.birthDay?.toByte(), item.patient.birthMonth?.toByte(), item.patient.birthYear?.toShort(), item.patient.name, item.patient.sex)
                        .onDuplicateKeyUpdate()
                        .set(PATIENT.BIRTH_DAY, item.patient.birthDay?.toByte())
                        .set(PATIENT.BIRTH_MONTH, item.patient.birthMonth?.toByte())
                        .set(PATIENT.BIRTH_YEAR, item.patient.birthYear?.toShort())
                        .returning()
                }.toMono()
            ).flatMap {
                trx.dsl()
                    .insertInto(ORDER)
                    .columns(ORDER.ID, ORDER.CREDIT, ORDER.OUTSOURCING_COST, ORDER.PRICE, ORDER.TEST, ORDER.USER_ID)
                    .values(UUID.randomUUID(), dto.credit, dto.outsourcingCost, dto.price, dto.test, userId)
                    .returningResult(ORDER.ID)
            }.flatMap { orderId ->
                dto.id = orderId.value1()
                Flux.fromIterable(dto.items).flatMap { item ->
                    val itemId =trx.dsl()
                        .insertInto(ITEM)
                        .columns(ITEM.ID, ITEM.ORDER_AT, ITEM.ORDER_ID, ITEM.ORGANIZATION_ID, ITEM.PATIENT_SERIAL, ITEM.USER_ID, ITEM.SERVICE_ID)
                        .values(UUID.randomUUID(), LocalDateTime.now(), orderId.value1(), userId, item.patient.serial, userId, item.service)
                        .returningResult(ITEM.ID)
                    Flux.fromIterable(item.patient.samples!!).zipWith(itemId).flatMap { itemSample ->
                        item.id = itemSample.t2.value1()
                        val sampleId = trx.dsl()
                            .insertInto(SAMPLE)
                            .columns(
                                SAMPLE.ID,
                                SAMPLE.ORGANIZATION_ID,
                                SAMPLE.PATIENT_SERIAL,
                                SAMPLE.USER_ID,
                                SAMPLE.SAMPLE_TYPE_ID,
                                SAMPLE.AGE,
                                SAMPLE.DEPARTMENT,
                                SAMPLE.NOTE,
                                SAMPLE.REGISTRATION_AT,
                                SAMPLE.SAMPLING,
                                SAMPLE.SERIAL,
                                SAMPLE.STATE,
                                SAMPLE.WARD,
                                SAMPLE.PHYSICIAN,
                                SAMPLE.ITEM_ID)
                            .values(
                                UUID.randomUUID(),
                                userId,
                                item.patient.serial,
                                userId,
                                itemSample.t1.typeId,
                                itemSample.t1.age,
                                itemSample.t1.department,
                                itemSample.t1.note,
                                LocalDateTime.now(),
                                itemSample.t1.sampling!!.atStartOfDay(),
                                itemSample.t1.serial,
                                "REQUEST",
                                itemSample.t1.ward,
                                itemSample.t1.physician,
                                itemSample.t2.value1(),
                            ).returningResult(SAMPLE.ID)
                        itemSample.t1.extensions?.let {
                            itemSample.t1.id = itemSample.t2.value1()
                            Flux.fromIterable(it).zipWith(sampleId).flatMap { sampleExtension ->
                                trx.dsl()
                                    .insertInto(SAMPLE_EXTENSION)
                                    .columns(SAMPLE_EXTENSION.EXTENSION_ID, SAMPLE_EXTENSION.SAMPLE_ID, SAMPLE_EXTENSION.VALUE)
                                    .values(sampleExtension.t1.id, sampleExtension.t2.value1(), sampleExtension.t1.value)
                            }
                        }?: run {
                            itemSample.t1.id = itemSample.t2.value1()
                            sampleId.toMono()
                        }
                    }
                }
            }
        }).map { dto }
    }

    fun findOrders(userId: String): Flux<Order_> {
        val query = dslContext.select(ORDER.ID).from(ORDER)
        return Flux.from(query).map { record ->
            Order_(
                id = record.getValue("id", UUID::class.java)
            )
        }
    }
    /*fun findOrders(userId: String): Flux<Order_> {
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

    }*/

    fun findOrder(ordersId: UUID): Mono<Order> {
        return orderRepo.findOne(item.orderId.eq(ordersId))
    }

    fun deleteOrder(sampleId: UUID, itemId: UUID, orderId: UUID): Mono<CancelOrder_> =
        orderRepo.findById(orderId).flatMap { order -> orderRepo.delete(order)
            .then(Mono.just(CancelOrder_(sampleId, "의뢰 취소 되었습니다.")
            .apply {this.itemId=itemId; this.orderId=orderId}))
        }
}
