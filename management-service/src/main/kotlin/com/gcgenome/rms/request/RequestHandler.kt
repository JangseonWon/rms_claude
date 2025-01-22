package com.gcgenome.rms.user

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.Page
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.RequestDTO
import com.gcgenome.rms.data.Role
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class RequestHandler(
    val dslContext: DSLContext,
): RequestDao, RequestGroupDao, SampleExtensionDao, SampleDao, PatientDao, ReportDao{
    fun searchRequests(query: Query): Mono<Page<RequestDTO>> {
        return dslContext.selectRequestsWithPage(query)
    }

    fun deleteRequests(requests: Array<RequestDTO>): Flux<Void> {
        return Flux.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                Flux.fromArray(requests).flatMap { request ->
                    deleteReportByServiceIdAndSampleId(request.service!!.id!!, request.sample!!.id!!)
                        .then(deleteRequestById(request))
                        .then(deleteRequestGroupById(request.requestGroup!!.id!!))
                        .then(deleteSampleExtensionBySampleId(request.sample!!.id!!))
                        .then(deleteSampleById(request.sample!!.id!!))
                        .then(deletePatientById(request.sample!!.patient!!, request.user!!.id))
                        .then()
                }
            }
        })
    }
}