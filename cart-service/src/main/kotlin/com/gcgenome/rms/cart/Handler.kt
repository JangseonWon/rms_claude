package com.gcgenome.rms.cart

import com.gcgenome.rms.authentication.User
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
    fun requests(user: User, query: Query): Mono<Pair<List<Request>, Page>> {
        return dslContext.selectRequestCountByUserId(user)
            .flatMap { count ->
                val adjustedQuery = query.apply { page.number -= 1 }
                val requests = dslContext.selectRequestByUserId(user, adjustedQuery)
                val totalPages = Mono.just((count + query.page.size - 1) / query.page.size)
                Mono.zip(requests.collectList(), totalPages) { request, pageCount ->
                    Pair(request, Page(query.page.size, query.page.number + 1, pageCount, count))
                }
            }
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
    fun cartToOrder(request: Request): Mono<Request> {
        return Mono.from(dslContext.transactionPublisher{trx ->
            trx.dsl().run {
                selectOrderMaxSerialByUserId(request.sample!!.patient!!.organization!!.user!!.id!!)
                    .flatMap { serial -> updateOrderSerialAndCreatedAtById(request.orderId!!, serial) }
                    .then(updateRequestStatusAndCreateAtById(request.orderId!!, request.sampleId!!, request.serviceId!!))
                    .then(selectSampleMaxBarcodeById(request.sample!!.id!!, request.sample!!.patient!!.organization!!.user!!.branchSerial!!) )
                    .flatMap { barcode -> updateSampleBarcodeAndCreateAtById(request.sampleId!!, barcode) }
                    .then(selectRequestById(request.orderId!!, request.sampleId!!, request.serviceId!!))
            }
        })
    }
    fun sampleTypes(serviceId: String): Flux<SampleType> {
        return dslContext.selectSampleTypeByServiceId(serviceId)
    }
    fun deleteCart(orderId: UUID, sampleId: UUID, serviceId: String): Mono<Void> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                selectRequestById(orderId, sampleId, serviceId)
                    .flatMap { request ->
                        deleteRequest(request)
                            .then(deleteOrderById(request.orderId!!))
                            .then(deleteSampleExtensionBySampleId(request.sample!!.id!!))
                            .then(deleteSampleById(request.sample!!.id!!))
                            .then(deletePatientById(request.sample!!.patient!!))
                            .then()
                    }

            }
        })
    }
}