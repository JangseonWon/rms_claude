package com.gcgenome.rms.cart

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.*
import com.gcgenome.rms.exceptions.OrderNotFoundException
import org.jooq.Configuration
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Component
class Handler(val dslContext: DSLContext ) :
    RequestDao, OrganizationDao, SampleTypeDao, PatientDao, SampleDao, SampleExtensionDao
{
    fun getCartInfo(sampleId: UUID, serviceId: String): Mono<RequestDTO> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                selectRequestById(sampleId, serviceId)
                .switchIfEmpty(Mono.error(OrderNotFoundException()))
            }
        })
    }
    fun requestSearch(user: UserAuthentication, query: Query): Mono<Page<RequestDTO>> {
        return dslContext.selectRequestCartByUserId(user.user, query)
    }
    fun organizations(userId: String): Flux<OrganizationDTO> {
        return dslContext.selectOrganizationByUserId(userId)
    }
    fun updateRequest(request: RequestDTO): Mono<RequestDTO> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                selectRequestById(request.sample!!.id!!, request.service!!.id!!)
                    .flatMap { r->
                        insertPatient(request.user?.id!!, request.sample.patient!!)
                            .then(updateSample(request.sample))
                            .then(deletePatientById(request.user.id!!, r.sample!!.patient!!))
                            .then(processExtensions(trx, request.sample.id!!, request.sample.extensions))
                            .then(updateRequest(request))
                            .then(selectRequestById(request.sample.id!!, request.service.id!!))
                    }
            }
        })
    }
    fun cartToOrder(requests: Array<RequestDTO>): Flux<RequestDTO> {
        return Flux.from(dslContext.transactionPublisher{trx ->
            trx.dsl().run {
                Flux.fromArray(requests).flatMap { request ->
                    updateRequestStatusAndCreateAtById(request.sample!!.id!!, request.service!!.id!!)
                        .then(updateSampleBarcodeAndCreateAtById(request.sample.id!!, request.sample.patient!!.organization!!.user!!.branchSerial!!))
                        .then(selectRequestById(request.sample.id!!, request.service.id!!))
                }
            }
        })
    }
    fun sampleTypes(serviceId: String): Flux<SampleTypeDTO> {
        return dslContext.selectSampleTypeByServiceId(serviceId)
    }
    fun deleteCart(requests: Array<RequestDTO>): Flux<RequestDTO> {
        return Flux.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                Flux.fromArray(requests).flatMap { request ->
                        deleteRequest(request)
                            .then(deleteSampleExtensionBySampleId(request.sample!!.id!!))
                            .then(deleteSampleById(request.sample.id!!))
                            .then(deletePatientById(request.user?.id!!, request.sample.patient!!))
                            .then(selectRequestById(request.sample.id!!, request.service!!.id!!))
                }
            }
        })
    }
    private fun processExtensions(trx: Configuration, sampleId: UUID, extensions: List<ExtensionDTO>?): Mono<Void> {
        return extensions
            ?.takeIf { it.isNotEmpty() }
            ?.let {
                Flux.fromIterable(it)
                    .flatMap { extension ->
                        trx.dsl().updateSampleExtensionBySampleId(sampleId, extension)
                    }
                    .then()
            }
            ?: Mono.empty()
    }
}