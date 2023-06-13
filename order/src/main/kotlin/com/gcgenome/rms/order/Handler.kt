package com.gcgenome.rms.order

import com.gcgenome.lims.tables.references.ITEM
import com.gcgenome.lims.tables.references.ORDER
import com.gcgenome.lims.tables.references.SAMPLE
import com.gcgenome.lims.tables.references.SAMPLE_EXTENSION
import com.gcgenome.rms.data.CancelOrder_
import com.gcgenome.rms.data.Order_
import org.jooq.DSLContext
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.util.*

@Service("com.gcgenome.rms.order.Handler")
class Handler(
    private val orderDao: OrderDao,
    private val itemDao: ItemDao,
    private val patientDao: PatientDao,
    private val sampleDao: SampleDao,
    private val extensionDao: ExtensionDao,
    val dslContext: DSLContext
) {
    fun insertOrder(userId: String, dto: Order_): Mono<Order_> {
        return Mono.from(dslContext.transactionPublisher{ trx ->
            Flux.from(
                Flux.fromIterable(dto.items).flatMap {
                    patientDao.insertPatient(trx, userId, it.patient)
                }.toMono()
            ).flatMap { orderDao.insertOrder(trx, userId, dto) }
            .flatMap { orderId ->
                dto.id = orderId.getValue(ORDER.ID)!!
                Flux.fromIterable(dto.items).flatMap {
                    val item = itemDao.insertItem(trx, userId, orderId.value1()!!, it)
                    Flux.fromIterable(it.patient.samples!!).zipWith(item).flatMap { itemSample ->
                        it.id = itemSample.t2.value1()
                        val sampleId = sampleDao.insertSample(trx, it.patient.serial, userId, itemSample.t2.getValue(ITEM.ID)!!, itemSample.t1 )
                        itemSample.t1.extensions?.let {
                            itemSample.t1.id = itemSample.t2.value1()
                            Flux.fromIterable(it).zipWith(sampleId).flatMap { sampleExtension ->
                                extensionDao.insertSampleExtension(trx, sampleExtension.t1, sampleExtension.t2.getValue(SAMPLE.ID)!!)
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
        return orderDao.findOrders(userId)
    }

    fun cancel(sampleId: UUID): Mono<CancelOrder_> {
        return sampleDao.countSample(sampleId).flatMap {
            if (it > 1) {
                sampleDao.deleteSample(sampleId)
            }
            else {
                sampleDao.findSample(sampleId)
                    .flatMap { sample -> itemDao.findItem(sample.itemId) }
                    .flatMap { item -> itemDao.countItem(item.id)
                        .flatMap { orderCount ->
                            if(orderCount > 1 ) {
                                sampleDao.deleteSample(sampleId)
                                    .then(itemDao.deleteItem(sampleId, item.id))
                            } else {
                                itemDao.countMrn(item.patientSerial).flatMap { mrnCount ->
                                    if (mrnCount > 1) {
                                        sampleDao.deleteSample(sampleId)
                                            .then(itemDao.deleteItem(sampleId, item.id))
                                            .then(orderDao.deleteOrder(sampleId,item.id,item.orderId))
                                    } else{ sampleDao.deleteSample(sampleId)
                                        .then(itemDao.deleteItem(sampleId, item.id))
                                        .then(orderDao.deleteOrder(sampleId,item.id,item.orderId))
                                        .then(patientDao.deletePatient(sampleId, item.id, item.orderId, item.patientSerial))
                                    }}
                            }
                        }}
            }
        }
    }
}