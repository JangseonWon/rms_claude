package com.gcgenome.rms.order

import com.gcgenome.lims.tables.references.ITEM
import com.gcgenome.lims.tables.references.ORDER
import com.gcgenome.lims.tables.references.SAMPLE
import com.gcgenome.rms.data.Item_

import com.gcgenome.rms.data.Order_
import org.jooq.DSLContext
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import java.util.*

@Service("com.gcgenome.rms.order.Handler")
class Handler(
    val dslContext: DSLContext
): PatientDao, OrderDao, ItemDao, ExtensionDao, SampleDao {
    fun insertOrder(userId: String, dto: Order_): Mono<Order_> {
        return Mono.from(dslContext.transactionPublisher{ trx ->
            trx.dsl().run {
                Flux.from(
                    Flux.fromIterable(dto.items).flatMap {
                        insertPatient(userId, it.patient)
                    }.toMono()
                ).flatMap { insertOrder(userId, dto) }
                    .flatMap { orderId ->
                        dto.id = orderId.getValue(ORDER.ID)!!
                        Flux.fromIterable(dto.items).flatMap {
                            val item = insertItem(userId, orderId.value1()!!, it)
                            Flux.fromIterable(it.patient.samples!!).zipWith(item).flatMap { itemSample ->
                                it.id = itemSample.t2.value1()
                                val sampleId = insertSample(it.patient.serial, userId, itemSample.t2.getValue(ITEM.ID)!!, itemSample.t1 )
                                itemSample.t1.extensions?.let {
                                    itemSample.t1.id = itemSample.t2.value1()
                                    Flux.fromIterable(it).zipWith(sampleId).flatMap { sampleExtension ->
                                        insertSampleExtension(sampleExtension.t1, sampleExtension.t2.getValue(SAMPLE.ID)!!)
                                    }
                                }?: run {
                                    itemSample.t1.id = itemSample.t2.value1()
                                    sampleId.toMono()
                                }
                            }
                        }
                    }
            }
        }).map { dto }
    }

    fun findOrders(userId: String): Flux<Order_> {
        return dslContext.dsl().selectOrders(userId)
    }
    fun addSample(userId: String, sampleId: UUID, dto: Item_): Mono<Order_> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                Mono.from(selectSampleById(sampleId))
                    .flatMap {
                        Mono.from(updatePatient(userId, dto.patient))
                            .then(Mono.from(insertSample(dto.patient.serial, userId, it.getValue(SAMPLE.ITEM_ID)!!, dto.patient.samples!!.first())))
                            .then(Mono.from(selectOrderById(sampleId)))
                    }
                /*itemId.toMono()
                    .then(Mono.from(updatePatient(userId, dto.patient)))
                    .then(Mono.from(insertSample(dto.patient.serial, userId, itemId!!, dto.patient.samples!!.first())))
                    .then(Mono.from(selectOrderById(sampleId)))*/
            }
        }) .map(Order_::toModel)
    }
}