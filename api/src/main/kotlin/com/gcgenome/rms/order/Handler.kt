package com.gcgenome.rms.order

import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.Item
import com.gcgenome.rms.data.Order
import com.gcgenome.rms.data.Sample
import com.gcgenome.rms.exceptions.*
import org.jooq.DSLContext
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.util.*

@Service("com.gcgenome.rms.order.Handler")
class Handler(
    val dslContext: DSLContext
): PatientDao, OrderDao, OrganizationDao, ItemDao, SampleExtensionDao, SampleDao, UserDao, UserServiceDao, ServiceSampleTypeDao {
    fun insertOrder(userId: String, dto: Order): Mono<Order> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                insertOrder(userId, dto).flatMap { orderRecord ->
                    Flux.fromIterable(dto.items!!.toList()).flatMap { items ->
                        selectUserServiceById(userId, items.serviceId!!)
                        .switchIfEmpty(Mono.error(ServiceNotFoundException(items.serviceId)))
                        .then(insertOrganization(userId, items.patient!!.organization))
                        .then(insertPatient(items.patient.organization?.id ?: userId, userId, items.patient))
                        .then(insertItem(userId, orderRecord.id!!, items.patient.organization?.id ?: userId, items))
                        .flatMap { itemRecord ->
                            Flux.fromIterable(items.patient.samples!!.toList()).flatMap { sample ->
                                selectServiceSampleTypeById(sample.sampleTypeId, items.serviceId)
                                .switchIfEmpty(Mono.error(ServiceSampleTypeNotFoundException(sample.sampleTypeId, items.serviceId)))
                                .then(selectUserById(userId))
                                .flatMap { user ->
                                    selectSamplePostfix(user.code).flatMap { postfix ->
                                        insertSample(items.patient.serial!!, userId, itemRecord.id!!, sample, items.patient.organization?.id ?: userId, user.code, postfix+1)
                                            .flatMap { sampleRecord ->
                                                Flux.fromIterable(sample.extensions?.toList() ?: listOf())
                                                    .flatMap { extension -> insertSampleExtension(extension, sampleRecord.id!!) }
                                                    .toMono()
                                            }
                                    }
                                }
                            }.toMono()
                        }
                    }.then(selectOrderById(orderRecord.id!!))
                }
            }
        })
    }
    fun findOrder(sampleId: UUID, userId: String): Mono<Order> {
        return Mono.from(dslContext.transactionPublisher { trx->
            trx.dsl().run {
                selectSampleById(sampleId)
                    .switchIfEmpty(Mono.error(SampleNotFoundException(sampleId)))
                    .flatMap { selectItemById(it.itemId!!) }
                    .flatMap { selectOrderById(it.orderId!!) }
            }
        })
    }
    fun addSample(userId: String, sampleId: UUID, dto: Array<Sample>): Mono<Order> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                selectSampleById(sampleId)
                    .switchIfEmpty(Mono.error(SampleNotFoundException(sampleId)))
                    .zipWith(selectUserById(userId))
                    .flatMap {
                        Flux.fromIterable(dto.toList())
                            .flatMap {sample ->
                                selectSamplePostfix(it.t2.code).flatMap { postfix ->
                                    insertSample(it.t1.patientSerial!!, userId, it.t1.itemId!!, sample, it.t1.organizationId!!, it.t2.code, postfix+1)
                                        .flatMap { sampleRecord ->
                                            Flux.fromIterable(sample.extensions?.toList() ?: listOf())
                                                .flatMap { extension -> insertSampleExtension(extension, sampleRecord.id!!) }
                                                .toMono()
                                        }
                                }
                            }.then(selectItemById(it.t1.itemId!!))
                    }.flatMap {
                        selectOrderById(it.orderId!!)
                    }
            }
        })
    }

    fun addItem(userId: String, itemId: UUID, dto: Array<Item>): Mono<Order> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                selectItemById(itemId)
                    .switchIfEmpty(Mono.error(ItemNotFoundException(itemId)))
                    .flatMap {
                        Flux.fromIterable(dto.toList()).flatMap { items ->
                            selectUserServiceById(userId, items.serviceId!!)
                                .switchIfEmpty(Mono.error(ServiceNotFoundException(items.serviceId)))
                                .then(insertOrganization(userId, items.patient!!.organization))
                                .then(insertPatient(items.patient.organization?.id ?: userId, userId, items.patient))
                                .then(insertItem(userId, it.orderId!!, items.patient.organization?.id ?: userId, items))
                                .flatMap { itemRecord ->
                                    Flux.fromIterable(items.patient.samples!!.toList()).flatMap { sample ->
                                        selectServiceSampleTypeById(sample.sampleTypeId, items.serviceId)
                                            .switchIfEmpty(Mono.error(ServiceSampleTypeNotFoundException(sample.sampleTypeId, items.serviceId)))
                                            .then(selectUserById(userId))
                                            .flatMap { user ->
                                                selectSamplePostfix(user.code).flatMap { postfix ->
                                                    insertSample(items.patient.serial!!, userId, itemRecord.id!!, sample, items.patient.organization?.id ?: userId, user.code, postfix+1)
                                                        .flatMap { sampleRecord ->
                                                            Flux.fromIterable(sample.extensions?.toList() ?: listOf())
                                                                .flatMap { extension -> insertSampleExtension(extension, sampleRecord.id!!) }
                                                                .toMono()
                                                        }
                                                }
                                            }
                                    }.toMono()
                                }
                        }.then(selectOrderById(it.orderId!!))
                    }
            }
        })
    }
    fun cancelOrder(sampleId: UUID): Mono<Any> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                selectSampleById(sampleId)
                    .switchIfEmpty(Mono.error(SampleNotFoundException(sampleId)))
                    .flatMap { sample ->
                        when(sample.state) {
                            "NEW" -> {
                                deleteSampleExtensionBySampleId(sampleId)
                                    .then(deleteSampleById(sampleId))
                                    .flatMap { deleteItemById(it.itemId!!) }
                                    .flatMap { item ->
                                        deletePatientById(item.patientSerial!!, item.organizationId!!, item.userId!!)
                                            .then(deleteOrderById(item.orderId!!))
                                    }
                            }else -> Mono.error(SampleDeleteException())
                        }
                    }
            }
        })
    }
}