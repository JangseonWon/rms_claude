package com.gcgenome.rms.cart

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.*
import com.gcgenome.rms.exceptions.OrderNotFoundException
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Component
class Handler(val dslContext: DSLContext ) :
    RequestDao, OrganizationDao, SampleTypeDao, PatientDao, SampleDao, OrderDao, SampleExtensionDao
{

    fun getCartInfo(orderId: UUID, sampleId: UUID, serviceId: String): Mono<Request> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                selectRequestById(orderId, sampleId, serviceId)
                .switchIfEmpty(Mono.error(OrderNotFoundException()))
            }
        })
    }
    fun requestSearch(user: UserAuthentication, query: Query): Mono<Page<RequestDTO>> {
        return dslContext.selectRequestCartByUserId(user.user, query)
    }
    fun organizations(userId: String): Flux<Organization> {
        return dslContext.selectOrganizationByUserId(userId)
    }
    fun updateRequest(request: Request): Mono<Request> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                selectRequestById(request.orderId!!, request.sampleId!!, request.serviceId!!)
                    .flatMap { r->
                        insertPatient(request.sample!!.patient!!)
                            .then(updateSample(request.sample!!))
                            .then(deletePatientById(r.sample!!.patient!!))
                            .then(updateRequest(request))
                            .then(selectRequestById(request.orderId!!, request.sampleId!!, request.serviceId!!))
                    }
            }
        })
    }
    fun cartToOrder(requests: Array<Request>): Flux<Request> {
        return Flux.from(dslContext.transactionPublisher{trx ->
            trx.dsl().run {
                Flux.fromArray(requests).flatMap { request ->
                    updateOrderSerialAndCreatedAtById(request.orderId!!, request.sample!!.patient!!.organization!!.user!!.id!!)
                        .then(updateRequestStatusAndCreateAtById(request.orderId!!, request.sample!!.id!!, request.service!!.id!!))
                        .then(updateSampleBarcodeAndCreateAtById(request.sample!!.id!!, request.sample!!.patient!!.organization!!.user!!.branchSerial!!))
                        .then(selectRequestById(request.orderId!!, request.sample!!.id!!, request.service!!.id!!))
                }
            }
        })
    }
    fun sampleTypes(serviceId: String): Flux<SampleType> {
        return dslContext.selectSampleTypeByServiceId(serviceId)
    }
    fun deleteCart(requests: Array<Request>): Flux<Request> {
        return Flux.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                Flux.fromArray(requests).flatMap { request ->
                        deleteRequest(request)
                            .then(deleteOrderById(request.orderId!!))
                            .then(deleteSampleExtensionBySampleId(request.sample!!.id!!))
                            .then(deleteSampleById(request.sample!!.id!!))
                            .then(deletePatientById(request.sample!!.patient!!))
                            .then(selectRequestById(request.orderId!!, request.sample!!.id!!, request.service!!.id!!))

                }
            }
        })
    }
}