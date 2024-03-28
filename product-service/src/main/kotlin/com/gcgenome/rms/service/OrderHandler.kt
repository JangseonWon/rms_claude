package com.gcgenome.rms.service

import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.*
import com.gcgenome.rms.exceptions.OrganizationNotFoundException
import com.gcgenome.rms.exceptions.ServiceNotFoundException
import com.gcgenome.rms.exceptions.ServiceSampleTypeNotFoundException
import com.gcgenome.rms.tables.records.OrganizationRecord
import com.gcgenome.rms.tables.records.UserServiceRecord
import org.jooq.Configuration
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class OrderHandler(
    val dslContext: DSLContext
): PatientDao, OrderDao, OrganizationDao, RequestDao, ExtensionDao, SampleDao, UserDao, UserServiceDao, ServiceSampleTypeDao {

    fun insertOrderProcess(userId: String, requests: List<Request>): Mono<Order> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                generateOrderSerial(userId, trx).flatMap { serial ->
                    val createTime = if (requests[0].status == "CART") null else LocalDateTime.now()
                    insertOrder(userId, serial, createTime).flatMap { order ->
                        Flux.fromIterable(requests).flatMap { request ->
                            insertPatientProcess(userId, request.patient!!, trx)
                                .then(insertSampleProcess(request.serviceId, request.patient.sample, createTime, trx))
                                .flatMap { sample ->
                                    request.apply {
                                        orderId = order.id
                                        sampleId = sample.id
                                        createAt = createTime
                                    }
                                    insertRequestProcess(userId, request, trx)
                                }
                        }.then(selectOrderById(order.id!!))
                    }
                }
            }
        })
    }

    fun checkUserServiceById(userId: String, serviceId: String, trx: Configuration): Mono<UserServiceRecord> {
        return trx.dsl().selectUserServiceById(userId, serviceId)
            .switchIfEmpty(Mono.error(ServiceNotFoundException(serviceId)))
    }

    fun checkOrganization(userId: String, organizationId: String, trx: Configuration): Mono<OrganizationRecord> {
        return trx.dsl().selectOrganization(userId, organizationId)
            .switchIfEmpty(Mono.error(OrganizationNotFoundException(userId, organizationId)))
    }

    fun checkServiceSampleTypeById(sampleTypeId: String, serviceId: String, trx: Configuration): Mono<Void> {
        return trx.dsl().selectServiceSampleTypeById(sampleTypeId, serviceId)
            .switchIfEmpty(Mono.error(ServiceSampleTypeNotFoundException(sampleTypeId, serviceId)))
            .then()
    }

    fun insertPatientProcess(userId: String, patient: Patient, trx: Configuration): Mono<Void> {
        return trx.dsl().run {
            checkOrganization(userId, patient.organization.id!!, trx)
                .then(insertPatient(patient.organization.id, userId, patient)).
                    then()
        }
    }

    fun insertSampleProcess(serviceId: String, sampleDto: Sample, createTime: LocalDateTime?, trx: Configuration): Mono<Sample> {
        return trx.dsl().run {
            checkServiceSampleTypeById(sampleDto.sampleTypeId!!, serviceId, trx)
                .then(insertSample(sampleDto, createTime)
                    .flatMap { sample ->
                        insertSampleExtensionProcess(sample.id!!, sampleDto.extensions, trx)
                            .then(selectSampleById(sample.id!!))
                    })
        }
    }

    fun insertRequestProcess(userId: String, request: Request, trx: Configuration): Mono<Request> {
        return trx.dsl().run {
            if (request.status == Status.CART.toString()) request.apply { cartAt = LocalDateTime.now() }
            checkUserServiceById(userId, request.serviceId, trx)
                .then(insertRequest(request)
            )
        }
    }

    fun insertSampleExtensionProcess(sampleId: UUID, extensions: List<Extension>?, trx: Configuration): Mono<Void> {
        return trx.dsl().run {
            Flux.fromIterable(extensions ?: emptyList())
                .flatMap { extension -> insertSampleExtension(extension, sampleId) }
                .then()
        }
    }

    fun generateSampleBarcode(userId: String, trx: Configuration): Mono<String> {
        val todayBarcode = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        return trx.dsl().run {
            selectUserById(userId).flatMap { user ->
                val barcodePrefix = "$todayBarcode${user.branchSerial}"
                selectSampleBarcode(barcodePrefix)
                    .flatMap { barcode ->
                        if (barcode != null) {
                            Mono.just(barcode.toLong().plus(1).toString())
                        } else {
                            Mono.just("${barcodePrefix}5001")
                        }
                    }
            }
        }
    }

    fun generateOrderSerial(userId: String, trx: Configuration): Mono<String> {
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val serialPrefix = "$userId$today"
        return trx.dsl().selectOrderSerial(serialPrefix)
            .map { serial ->
                val incrementedSerial = (serial!!.toLong().plus(1)).toString()
                incrementedSerial
            }
            .switchIfEmpty(Mono.just("${serialPrefix}0001"))
    }
}