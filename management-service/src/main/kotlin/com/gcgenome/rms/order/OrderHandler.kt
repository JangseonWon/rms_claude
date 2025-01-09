package com.gcgenome.rms.user

import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.RequestDTO
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Component
class OrderHandler(
    val dslContext: DSLContext,
): RequestDao, SampleExtensionDao, SampleDao, PatientDao, ReportDao{
    fun deleteOrder(requests: Array<RequestDTO>): Flux<Void> {
        return Flux.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                Flux.fromArray(requests).flatMap { request ->
                    deleteReportByServiceIdAndSampleId(request.service!!.id!!, request.sample!!.id!!)
                        .then(deleteRequestById(request))
                        .then(deleteSampleExtensionBySampleId(request.sample!!.id!!))
                        .then(deleteSampleById(request.sample!!.id!!))
                        .then(deletePatientById(request.sample!!.patient!!))
                        .then()
                }
            }
        })
    }
}