package com.gcgenome.rms.order

import com.gcgenome.rms.dao.*
import com.gcgenome.rms.tables.references.SAMPLE
import com.gcgenome.rms.data.CancelOrder
import com.gcgenome.rms.data.Item
import com.gcgenome.rms.data.Order
import com.gcgenome.rms.data.User
import com.gcgenome.rms.tables.records.UserRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.util.*

@Service("com.gcgenome.rms.order.Handler")
class Handler(
    val dslContext: DSLContext
): PatientDao, OrderDao, OrganizationDao, ItemDao, ExtensionDao, SampleDao, UserDao {
    fun insertOrder(userId: String, dto: Order): Mono<Order> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                insertOrder(userId, dto).flatMap { orderRecord ->
                    Flux.fromIterable(dto.items!!).flatMap { items ->
                        insertOrganization(userId, items.patient.organization)
                        .then(insertPatient(items.patient.organization?.id ?: userId, userId, items.patient))
                        .then(insertItem(userId, orderRecord.id!!, items.patient.organization?.id ?: userId, items))
                        .flatMap { itemRecord ->
                            Flux.fromIterable(items.patient.samples!!).flatMap { sample ->
                                selectUserById(userId).flatMap { userRecord ->
                                    selectSamplePostfix(userRecord.code!!).flatMap { postfix ->
                                        insertSample(items.patient.serial, userId, itemRecord.id!!, sample, items.patient.organization?.id ?: userId, userRecord.code!!, postfix+1)
                                            .flatMap { sampleRecord ->
                                                Flux.fromIterable(sample.extensions ?: listOf())
                                                    .flatMap { extension -> insertSampleExtension(extension, sampleRecord.id!!) }
                                                    .toMono()
                                            }
                                    }
                                }
                            }.toMono()
                        }
                    }.then(selectOrderById(orderRecord.id!!)).map(Order::toModel)
                }
            }
        })
    }

    fun findOrders(userId: String): Flux<Order> {
        return dslContext.dsl().selectOrders(userId)
    }
    fun findOrder(sampleId: UUID): Mono<Order> {
        return Mono.from(dslContext.transactionPublisher { trx->
            trx.dsl().run {
                selectSampleById(sampleId)
                    .flatMap { selectItemById(it.itemId!!) }
                    .flatMap { selectOrderById(it.orderId!!) }
                    .map(Order::toModel)
            }
        })
    }
    fun addSample(userId: String, sampleId: UUID, dto: Item): Mono<Order> {
        return Mono.empty()
        /*return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                selectSampleById(sampleId).flatMap { sampleRecord ->
                    updatePatientById(userId, dto.patient)
                        .then(
                            if (dto.patient.organization != null) {
                                insertOrganization(userId, dto.patient.organization)
                            } else {
                                Mono.empty()
                            }
                        )
                        .then(
                            Flux.fromIterable(dto.patient.samples!!).flatMap { sample ->
                                insertSample(
                                    dto.patient.serial, userId, sampleRecord.getValue(SAMPLE.ITEM_ID)!!, sample!!,
                                    dto.patient.organization?.id ?: userId
                                )
                                    .flatMap { record ->
                                        Flux.fromIterable(sample.extensions ?: listOf()).flatMap { extension ->
                                            insertSampleExtension(extension, record.id!!)
                                        }.toMono()
                                    }
                            }.toMono()
                        )
                        .then(
                            Mono.from(selectItemById(sampleRecord.getValue(SAMPLE.ITEM_ID)!!))
                                .flatMap { record ->
                                    selectOrderById(record.orderId!!)
                                }
                        )
                }
            }
        }).map { record ->
            if (dto.patient.organization != null) {
                Order.toModel(record)
            } else {
                Order.toModel(record, dto)
            }
        }*/
    }

    fun updateOrder(userId: String, itemId: UUID, dto: Item): Mono<Order> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                updateItemById(itemId, dto)
                    .then(updatePatientById(userId, dto.patient))
                    .thenMany(
                        Flux.fromIterable(dto.patient.samples ?: listOf()).flatMap { sample ->
                            updateSampleById(sample)
                                .thenMany(
                                    Flux.fromIterable(sample.extensions ?: listOf()).flatMap { extension ->
                                        deleteSampleExtensionBySampleId(sample.id!!)
                                            .then(insertSampleExtension(extension, sample.id!!))
                                    })
                        })
                    .then(Mono.from(selectItemById(itemId))).flatMap { item ->
                        selectOrderById(item.orderId!!)
                    }
            }
        }).map { record ->
            if (dto.patient.organization != null) {
                Order.toModel(record)
            } else {
                Order.toModel(record, dto)
            }
        }
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