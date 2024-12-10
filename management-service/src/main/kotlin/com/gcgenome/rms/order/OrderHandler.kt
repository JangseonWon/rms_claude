package com.gcgenome.rms.user

import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.RequestDTO
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Component
class OrderHandler(
    val dslContext: DSLContext,
): RequestDao, OrderDao, SampleExtensionDao, SampleDao, PatientDao, ReportDao{
    fun deleteOrder(requests: Array<RequestDTO>): Flux<RequestDTO> {
        return Flux.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                Flux.fromArray(requests).flatMap { request ->
                    deleteReportByOrderId(request.order?.id!!)
                        .then(deleteRequest(request))
                        .then(deleteOrderById(request.order?.id!!))
                        .then(deleteSampleExtensionBySampleId(request.sample!!.id!!))
                        .then(deleteSampleById(request.sample!!.id!!))
                        .then(deletePatientById(request.sample!!.patient!!))
                        .then(selectRequestById(request.sample!!.id!!, request.service!!.id!!))
                }
            }
        })
    }
}