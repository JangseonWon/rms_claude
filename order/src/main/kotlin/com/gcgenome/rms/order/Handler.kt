package com.gcgenome.rms.order

import com.gcgenome.rms.data.Order_
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service("com.gcgenome.rms.order.Handler")
class Handler(
    private val orderdao: OrderDao,
    private val itemDao: ItemDao,
    private val patientDao: PatientDao,
    private val sampleDao: SampleDao,
    private val extensionDao: ExtensionDao
) {
    //@Transactional
    fun order(userId: String, dto:Order_): Mono<Order_> {
        return orderdao.saveOrder(userId, dto)
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
        return orderdao.findOrders(userId)
    }
}