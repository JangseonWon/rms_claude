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
                    Flux.fromIterable(dto.items!!)
                        .flatMap { Mono.from(insertPatient(userId, it.patient)) }
                        .then(Mono.from(insertOrder(userId, dto)))
                ).flatMap { order ->
                    Flux.fromIterable(dto.items).flatMap {
                        val item = insertItem(userId, order.value1()!!, it)
                        Flux.fromIterable(it.patient.samples!!).zipWith(item).flatMap { itemSample ->
                            Mono.from(insertSample(it.patient.serial, userId, itemSample.t2.getValue(ITEM.ID)!!, itemSample.t1))
                                .flatMap {sample ->
                                    Flux.fromIterable(itemSample.t1.extensions ?: listOf())
                                        .flatMap { extension ->
                                            insertSampleExtension(extension, sample.id!!)
                                        }.toMono()
                                }
                            }
                    }.then(Mono.from(selectOrderById(order.get(ORDER.ID)!!)))
                }
            }
        }).map(Order_::toModel)
    }

    fun findOrders(userId: String): Flux<Order_> {
        return dslContext.dsl().selectOrders(userId)
    }
    fun addSample(userId: String, sampleId: UUID, dto: Item_): Mono<Order_> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                Mono.from(selectSampleById(sampleId))
                    .flatMap {
                        Mono.from(updatePatientById(userId, dto.patient))
                            .then(
                                Mono.from(insertSample(dto.patient.serial, userId, it.value1().getValue(SAMPLE.ITEM_ID)!!, dto.patient.sample!!))
                                    .flatMap { record ->
                                        dto.patient.sample.extensions?.let { it1 ->
                                            Flux.fromIterable(it1).flatMap { extension ->
                                                Mono.from(insertSampleExtension(extension, record.id!!))
                                            }.toMono()
                                        }?: run{ Mono.empty() }
                                    }
                            ).then (
                                Mono.from(selectItemById(it.value1().getValue(SAMPLE.ITEM_ID)!!))
                                    .flatMap {record ->
                                        Mono.from(selectOrderById(record.orderId!!))
                                    }
                            )
                    }
            }
        }).map(Order_::toModel)
    }

    fun updateOrder(userId: String, dto: Order_): Mono<Order_> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run{
                Mono.from(updateOrderById(dto))
                    .then(Flux.fromIterable(dto.items ?: listOf())
                        .flatMap{item ->
                            Mono.from(updateItemById(item))
                                .then(Mono.from(updatePatientById(userId, item.patient)))
                                .then(
                                    Flux.fromIterable(item.patient.samples ?: listOf()).flatMap { sample->
                                        Mono.from(updateSampleById(sample))
                                            .then(
                                                Flux.fromIterable(sample.extensions ?: listOf()).flatMap {extension ->
                                                    Mono.from(deleteSampleExtensionBySampleId(sample.id!!))
                                                        .then(
                                                            Mono.from(insertSampleExtension(extension, sample.id!!))
                                                        )
                                                }.toMono()
                                            )
                                    }.toMono()
                                )
                        }.toMono()
                    ).then(Mono.from(selectOrderById(dto.id!!)))
            }
        }).map(Order_::toModel)
    }
}