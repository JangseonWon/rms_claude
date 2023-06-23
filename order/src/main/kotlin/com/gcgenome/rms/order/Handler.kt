package com.gcgenome.rms.order

import com.gcgenome.lims.tables.references.ITEM
import com.gcgenome.lims.tables.references.ORDER
import com.gcgenome.lims.tables.references.SAMPLE
import com.gcgenome.rms.data.CancelOrder
import com.gcgenome.rms.data.Item
import com.gcgenome.rms.data.Order
import org.jooq.DSLContext
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.util.*

@Service("com.gcgenome.rms.order.Handler")
class Handler(
    val dslContext: DSLContext
): PatientDao, OrderDao, OrganizationDao, ItemDao, ExtensionDao, SampleDao {
    fun insertOrder(userId: String, dto: Order): Mono<Order> {
        return Mono.from(dslContext.transactionPublisher{ trx ->
            trx.dsl().run {
                Flux.from(
                    Flux.fromIterable(dto.items!!).flatMap {
                        if (it.patient.organization != null) {
                            insertOrganization(userId, it.patient.organization)
                                .then(insertPatient(it.patient.organization.id ?: userId, userId, it.patient))
                        } else {
                            insertPatient(it.patient.organization?.id ?: userId, userId, it.patient)
                        }
                    }.then(insertOrder(userId, dto))
                ).flatMap { order ->
                    Flux.fromIterable(dto.items).flatMap {
                        val item = insertItem(userId, order.value1()!!,it.patient.organization?.id ?: userId ,it)
                        Flux.fromIterable(it.patient.samples!!)
                            .zipWith(item)
                            .flatMap { itemSample ->
                            insertSample(it.patient.serial, userId, itemSample.t2.getValue(ITEM.ID)!!, itemSample.t1, it.patient.organization?.id)
                                .flatMap {sample ->
                                    Flux.fromIterable(itemSample.t1.extensions ?: listOf())
                                        .flatMap { extension ->
                                            insertSampleExtension(extension, sample.id!!)
                                        }.toMono()
                                }
                            }
                    }.then( if (dto.items.firstOrNull()?.patient?.organization != null) {
                        Mono.from(selectOrderById(order.get(ORDER.ID)!!))
                    } else { Mono.from(selectOrderNotINOrganization(order.get(ORDER.ID)!!))}
                    )
                }
            }
        }).map(Order::toModel)
    }

    fun findOrders(userId: String): Flux<Order> {
        return dslContext.dsl().selectOrders(userId)
    }
    fun addSample(userId: String, sampleId: UUID, dto: Item): Mono<Order> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                Mono.from(selectSampleById(sampleId))
                .flatMap {
                    updatePatientById(userId, dto.patient)
                        .then( if (dto.patient.organization != null) {
                            insertOrganization(userId, dto.patient.organization)
                        } else {
                            Mono.empty()
                        })
                        .then(
                            insertSample(dto.patient.serial, userId, it.getValue(SAMPLE.ITEM_ID)!!, dto.patient.sample!!, dto.patient.organization?.id)
                                .flatMap { record ->
                                    Flux.fromIterable(dto.patient.sample.extensions ?: listOf()).flatMap { extension ->
                                        insertSampleExtension(extension, record.id!!)
                                    }.toMono()
                                }
                        ).then (
                            Mono.from(selectItemById(it.getValue(SAMPLE.ITEM_ID)!!))
                                .flatMap {record ->
                                    if(dto.patient.organization != null) {
                                        Mono.from(selectOrderById(record.orderId!!))
                                    } else {
                                        Mono.from(selectOrderNotINOrganization(record.orderId!!))
                                    }
                                }
                        )
                }
            }
        }).map(Order::toModel)
    }

    fun updateOrder(userId: String, dto: Order): Mono<Order> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run{
                updateOrderById(dto)
                .then(Flux.fromIterable(dto.items ?: listOf()).flatMap{item ->
                    updateItemById(item)
                    .then(updatePatientById(userId, item.patient))
                    .then(
                        Flux.fromIterable(item.patient.samples ?: listOf()).flatMap { sample->
                            updateSampleById(sample)
                            .then(
                                Flux.fromIterable(sample.extensions ?: listOf()).flatMap {extension ->
                                    deleteSampleExtensionBySampleId(sample.id!!)
                                    .then(insertSampleExtension(extension, sample.id!!))
                                }.toMono()
                            )
                        }.toMono()
                    )
                }.toMono()
                ).then(if (dto.items?.firstOrNull()?.patient?.organization != null) {
                        Mono.from(selectOrderById(dto.id!!))
                    } else { Mono.from(selectOrderNotINOrganization(dto.id!!))})
            }
        }).map(Order::toModel)
    }

    fun cancelOrder(sampleId: UUID): Mono<CancelOrder> {
        val cancelFinish = CancelOrder(sampleId = sampleId, message = "취소 완료 하였습니다.")
        val notExistSampleId = Mono.just(CancelOrder(sampleId, "존재하지 않는 샘플입니다."))
        return dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                findSampleValue(sampleId).flatMap { value ->
                    val state = value.first
                    val itemId = value.second
                    countSample(itemId).flatMap { count ->
                        when {
                            count > 1 -> deleteExtension(sampleId)
                                .then(deleteSample(sampleId)).flatMap { Mono.just(cancelFinish) }
                            count == 1 -> {
                                when (state) {
                                    "REPORTED", "COMPLETE" ->
                                        Mono.just(CancelOrder(sampleId, "완료되어 취소가 불가능합니다. 관련 추가 문의는 GC지놈에 연락바랍니다."))
                                    "REGISTRATION" ->
                                        Mono.just(CancelOrder(sampleId, "실험 중으로 취소가 불가능합니다. 관련 추가 문의는 GC지놈에 연락바랍니다."))
                                    else -> {
                                        findItemValue(itemId).flatMap { itemValue ->
                                            val orderId = itemValue.first
                                            val mrn = itemValue.second
                                            countItemInOrder(orderId).flatMap { countOrder ->
                                                if (countOrder > 1) {
                                                    deleteExtension(sampleId)
                                                        .then(deleteSample(sampleId))
                                                        .then(deleteItem(itemId))
                                                        .map {
                                                            cancelFinish.itemId = itemId
                                                            cancelFinish
                                                        }
                                                } else {
                                                    countSampleInItem(itemId).flatMap { itemCount ->
                                                        if (itemCount > 1) {
                                                            countExtension(sampleId).flatMap { extensionCount ->
                                                                if (extensionCount > 0) {
                                                                    deleteExtension(sampleId)
                                                                        .then(deleteSample(sampleId))
                                                                        .map { cancelFinish }
                                                                } else {
                                                                    deleteSample(sampleId).map { cancelFinish }
                                                                }
                                                            }
                                                        } else {
                                                            countItemInMrn(mrn).flatMap { mrnCount ->
                                                                if (mrnCount > 1) {
                                                                    deleteExtension(sampleId)
                                                                        .then(deleteSample(sampleId))
                                                                        .then(deleteItem(itemId))
                                                                        .then(deleteOrder(orderId))
                                                                        .map {
                                                                            cancelFinish.itemId = itemId
                                                                            cancelFinish.orderId = orderId
                                                                            cancelFinish
                                                                        }
                                                                } else {
                                                                    deleteExtension(sampleId)
                                                                        .then(deleteSample(sampleId))
                                                                        .then(deleteItem(itemId))
                                                                        .then(deleteOrder(orderId))
                                                                        .then(deletePatient(mrn))
                                                                        .map {
                                                                            cancelFinish.itemId = itemId
                                                                            cancelFinish.orderId = orderId
                                                                            cancelFinish
                                                                        }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            else -> notExistSampleId
                        }
                    }
                }
            }.switchIfEmpty(notExistSampleId)
        }.toMono()
    }
}