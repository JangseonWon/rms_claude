package com.gcgenome.rms.request

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.*
import com.gcgenome.rms.exception.OrderNotFoundException
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Component
class RequestHandler(
    val dslContext: DSLContext
): RequestDao, RequestGroupDao, SampleExtensionDao, SampleDao, PatientDao {
    fun selectRequests(user: UserAuthentication, query: Query):  Mono<Page<RequestDTO>> {
        return dslContext.selectRequestsWithPage(query, user.user)
    }
    fun updateRequests(requests: List<RequestDTO>):Mono<Void> {
        return Flux.fromIterable(requests)
            .flatMap { request -> dslContext.updateRequest(request) }.then()
    }
    fun deleteRequests(requests: List<RequestDTO>):Mono<Void> {
        return Flux.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                Flux.fromIterable(requests).flatMap { request ->
                    deleteRequest(request)
                        .then(deleteRequestGroup(request.requestGroup!!.id!!))
                        .then(deleteSampleExtensionBySampleId(request.sample!!.id!!))
                        .then(deleteSampleById(request.sample!!.id!!))
                        .then(deletePatientById(request.user?.id!!, request.sample!!.patient!!))
                }
            }
        }).then()
    }
    fun getOrderInfo(sampleId: UUID, serviceId: String): Mono<RequestDTO> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                selectRequestBySampleIdAndService(sampleId, serviceId)
                    .switchIfEmpty(Mono.error(OrderNotFoundException()))
            }
        })
    }
}