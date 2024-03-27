package com.gcgenome.rms.order

import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.*
import com.gcgenome.rms.exceptions.*
import org.jooq.Configuration
import org.jooq.DSLContext
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service("com.gcgenome.rms.order.Handler")
class Handler(
    val dslContext: DSLContext
): PatientDao, OrderDao, RequestDao, SampleExtensionDao, SampleDao, UserDao, UserServiceDao {

    fun cancelOrder(serial:String, serviceId: String, barcode: String): Mono<Any> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                selectOrderBySerial(serial).switchIfEmpty(Mono.error(OrderNotFoundException(serial)))
                    .zipWith(selectSampleByBarcode(barcode).switchIfEmpty(Mono.error(SampleNotFoundException(barcode))))
                    .flatMap { selectRequestById(it.t1.id!!, serviceId,it.t2.id!!).switchIfEmpty(Mono.error(RequestNotFoundException(serial,serviceId,barcode))) }
                    .filter { it.status!! == Status.ORDERED.toString() }.switchIfEmpty(Mono.error(SampleDeleteException()))
                    .flatMap { removeRequest(it, trx) }
                    .flatMap { request ->
                        removeSample(request.sampleId!!, trx)
                            .flatMap { removePatient(Patient(it.organizationId!!, it.patientSerial!!, it.userId!!),trx) }
                            .flatMap { removeOrder(request.orderId!!,trx) }}
            }
        })
    }
    private fun removeRequest(request: Request, trx: Configuration): Mono<Request> {
        return Mono.from(trx.dsl().run {
            deleteRequestById(request)
        })
    }
    private fun removeSample(sampleId: UUID, trx: Configuration): Mono<Sample> {
        return Mono.from(trx.dsl().run {
            selectRequestBySampleId(sampleId).collectList()
                .filter{it.size == 0}
                .flatMap { deleteSampleExtensionBySampleId(sampleId)
                    .then(deleteSampleById(sampleId))}
        })
    }
    private fun removeOrder(orderId:UUID, trx: Configuration): Mono<Order> {
        return Mono.from(trx.dsl().run {
            deleteOrderById(orderId)
        })
    }
    private fun removePatient(patient: Patient, trx: Configuration): Mono<Patient> {
        return Mono.from(trx.dsl().run {
            deletePatientById(patient)
        })
    }
}