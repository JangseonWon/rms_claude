package com.gcgenome.rms.order

import com.gcgenome.rms.data.CancelOrder_
import com.gcgenome.rms.data.Order_
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service("com.gcgenome.rms.order.Handler")
class Handler(
    private val orderDao: OrderDao,
    private val itemDao: ItemDao,
    private val patientDao: PatientDao,
    private val sampleDao: SampleDao,
    private val extensionDao: ExtensionDao

) {
    //@Transactional
    fun order(userId: String, dto:Order_): Mono<Order_> {
        return orderDao.saveOrder(userId, dto)
            .flatMap {
                Flux.fromIterable(dto.items)
                    .flatMap { item->
                        patientDao.findPatient(userId, item.patient).switchIfEmpty(patientDao.savePatient(userId, item.patient)).map { item }
                    }
                    .flatMap { item->
                        itemDao.saveItem(userId, it.id!!, item)
                    }
                    .flatMap { item ->
                        Flux.fromIterable(item.patient.samples)
                            .flatMap { sample -> sampleDao.saveSample(userId, item, item.patient, sample) }
                            .flatMap { sample ->
                                Flux.fromIterable(sample.extensions)
                                    .flatMap { extension -> extensionDao.saveExtension(sample, extension) }
                            }
                    }
                    .then(
                        Mono.just(
                            it.apply {
                                items = dto.items
                            }
                        )
                    )
            }
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