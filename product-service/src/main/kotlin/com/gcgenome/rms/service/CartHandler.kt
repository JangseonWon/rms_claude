package com.gcgenome.rms.service

import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.Order
import com.gcgenome.rms.data.Request
import com.gcgenome.rms.data.Status
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Component
class CartHandler(
    val dslContext: DSLContext,
    val orderHandler: OrderHandler
): PatientDao, OrderDao, OrganizationDao, RequestDao, ExtensionDao, SampleDao, UserDao, UserServiceDao, ServiceSampleTypeDao {

    fun insertCartProcess(userId: String, requests: List<Request>): Mono<Order> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                val createTime = if (requests[0].status == "CART") null else LocalDateTime.now()
                    insertOrder(userId, null, createTime).flatMap { order ->
                        Flux.fromIterable(requests).flatMap { request ->
                            orderHandler.insertPatientProcess(userId, request.patient!!, trx)
                                .then(orderHandler.insertSampleProcess(request.serviceId, request.patient.sample, createTime, trx))
                                .flatMap { sample ->
                                    request.apply {
                                        orderId = order.id
                                        sampleId = sample.id
                                        createAt = createTime
                                    }
                                    orderHandler.insertRequestProcess(userId, request, trx)
                                }
                        }.then(selectOrderById(order.id!!))
                    }
            }
        })
    }
}