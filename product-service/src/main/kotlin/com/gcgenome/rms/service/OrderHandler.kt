package com.gcgenome.rms.service

import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.*
import com.gcgenome.rms.exception.ExtensionIdNotFoundException
import com.gcgenome.rms.exception.OrganizationNotFoundException
import com.gcgenome.rms.exception.ServiceNotFoundException
import com.gcgenome.rms.exception.ServiceSampleTypeNotFoundException
import com.gcgenome.rms.tables.pojos.SampleExtension
import com.gcgenome.rms.tables.pojos.ServiceSampleType
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

    fun insertOrderProcess(userId: String, orders: List<Order>): Flux<Order> {
        return Flux.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                Flux.fromIterable(orders).concatMap { order ->
                    generateOrderSerial(userId, trx).flatMap { orderSerial ->
                        var createTime: LocalDateTime? = null
                        var cartTime: LocalDateTime? = null
                        var serial: String? = null
                        if (checkCart(order)) {
                            createTime = LocalDateTime.now(); serial = orderSerial
                        } else { cartTime = LocalDateTime.now() }
                        insertOrder(userId, serial, createTime).flatMap { insertOrder ->
                            Flux.fromIterable(order.requests!!).flatMap { request ->
                                insertPatientProcess(userId, request.patient!!, trx)
                                    .then(insertSampleProcess(request.serviceId, request.patient.sample!!, createTime, trx))
                                    .flatMap { sample ->
                                        request.apply {
                                            orderId = insertOrder.id
                                            sampleId = sample.id
                                            createAt = createTime
                                            cartAt = cartTime
                                        }
                                        insertSampleExtensionProcess(request.serviceId, sample.id!!, request.patient.sample.extensions, trx)
                                            .then(insertRequestProcess(userId, request, trx))
                                    }
                            }.then(selectOrderById(insertOrder.id!!))
                        }
                    }
                }
            }
        })
    }

    fun checkCart(order: Order): Boolean {
        return order.requests?.any { it.status == Status.CART.toString() } ?: false
    }

    fun checkUserServiceById(userId: String, serviceId: String, trx: Configuration): Mono<UserServiceRecord> {
        return trx.dsl().selectUserServiceById(userId, serviceId)
            .switchIfEmpty(Mono.error(ServiceNotFoundException(serviceId)))
    }

    fun checkOrganization(userId: String, organizationId: String, trx: Configuration): Mono<OrganizationRecord> {
        return trx.dsl().selectOrganization(userId, organizationId)
            .switchIfEmpty(Mono.error(OrganizationNotFoundException(userId, organizationId)))
    }

    fun checkServiceSampleTypeById(sampleTypeId: String, serviceId: String, trx: Configuration): Mono<ServiceSampleType> {
        return trx.dsl().selectServiceSampleTypeById(sampleTypeId, serviceId)
            .switchIfEmpty(Mono.error(ServiceSampleTypeNotFoundException(sampleTypeId, serviceId)))
    }

    fun insertPatientProcess(userId: String, patient: Patient, trx: Configuration): Mono<Patient> {
        return trx.dsl().run {
            checkOrganization(userId, patient.organization?.id
                ?: throw OrganizationNotFoundException(userId, patient.organization?.id ?: ""), trx)
                .then(insertPatient(patient.organization.id, userId, patient))
        }
    }

    fun insertSampleProcess(serviceId: String, sampleDto: Sample, createTime: LocalDateTime?, trx: Configuration): Mono<Sample> {
        return trx.dsl().run {
            checkServiceSampleTypeById(sampleDto.sampleTypeId!!, serviceId, trx)
                .then(insertSample(sampleDto, createTime))
                .flatMap { sample -> selectSampleById(sample.id!!) }
        }
    }

    fun insertRequestProcess(userId: String, request: Request, trx: Configuration): Mono<Request> {
        return trx.dsl().run {
            checkUserServiceById(userId, request.serviceId, trx).then(insertRequest(request))
        }
    }

    fun insertSampleExtensionProcess(serviceId: String, sampleId: UUID, extensions: List<Extension>?, trx: Configuration): Flux<SampleExtension> {
        return trx.dsl().run {
            Flux.fromIterable(extensions ?: emptyList())
                .flatMap { extension -> checkExtensionIdByService(serviceId, extension.id!!)
                    .switchIfEmpty(Mono.error(ExtensionIdNotFoundException(serviceId, extension.id)))
                    .then(insertSampleExtension(extension, sampleId))
                }
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
        val serialPrefix = "${userId}${today}"
        return trx.dsl().selectOrderSerial(serialPrefix)
            .map { "${serialPrefix}${"%04d".format(it.takeLast(4).toLong() + 1)}" }
            .switchIfEmpty(Mono.just("${serialPrefix}0001"))
    }
}