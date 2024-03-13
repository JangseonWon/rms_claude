package com.gcgenome.rms.order

import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.Item
import com.gcgenome.rms.data.Sample
import com.gcgenome.rms.exceptions.ServiceNotFoundException
import com.gcgenome.rms.exceptions.ServiceSampleTypeNotFoundException
import com.gcgenome.rms.exceptions.OrganizationNotFoundException
import com.gcgenome.rms.tables.records.OrganizationRecord
import com.gcgenome.rms.tables.records.ServiceSampleTypeRecord
import com.gcgenome.rms.tables.records.UserServiceRecord
import org.jooq.Configuration
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class OrderHandler(
    val dslContext: DSLContext
): PatientDao, OrderDao, OrganizationDao, ItemDao, ExtensionDao, SampleDao, UserDao, UserServiceDao, ServiceSampleTypeDao {

    fun insertOrderRequest(userId: String, dto: List<Item>): Mono<List<Item>> {
    return Mono.from(dslContext.transactionPublisher { trx ->
        trx.dsl().run {
            Flux.fromIterable(dto).concatMap { items ->
                insertOrder(userId).flatMap { orderRecord ->
                    checkUserServiceById(userId, items.serviceId, trx)
                    .then(checkOrganization(userId, items.patient.organization.id, trx))
                    .then(insertPatient(items.patient.organization.id, userId, items.patient))
                    .then(insertItem(orderRecord.id!!, items.serviceId, items.serial))
                    .thenMany(Flux.fromIterable(items.patient.samples)
                        .concatMap { sample ->
                            checkServiceSampleTypeById(sample.sampleTypeId!!, items.serviceId, trx)
                                .then(selectUserById(userId))
                                .flatMap { userRecord ->
                                    generateSampleBarcode(userRecord.branchSerial, trx).flatMap { barcode ->
                                        insertSampleAndExtensions(userRecord.id, orderRecord.id!!, items.serviceId, sample, items.patient.serial,
                                                items.patient.organization.id, barcode!!, "ordered", trx)
                                    }
                                }
                        }
                    )
                    .then(selectItemById(orderRecord.id!!, userId))
                } }.collectList()
        } })
    }

    fun generateSampleBarcode(infix: String, trx: Configuration): Mono<String> {
        val todayBarcode = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val barcodePrefix = "$todayBarcode$infix"
        return trx.dsl().selectSampleBarcode(barcodePrefix)
            .map { barcode ->
                val incrementedBarcode = (barcode!!.toLong().plus(1)).toString()
                incrementedBarcode
            }
            .switchIfEmpty(Mono.just("${barcodePrefix}5001"))
    }

    fun checkUserServiceById(userId: String, serviceId: String, trx: Configuration): Mono<UserServiceRecord> {
        return trx.dsl().selectUserServiceById(userId, serviceId)
            .switchIfEmpty(Mono.error(ServiceNotFoundException(serviceId)))
    }

    fun checkOrganization(userId: String, organizationId: String, trx: Configuration): Mono<OrganizationRecord> {
        return trx.dsl().selectOrganization(userId, organizationId)
            .switchIfEmpty(Mono.error(OrganizationNotFoundException(userId, organizationId)))
    }

    fun checkServiceSampleTypeById(sampleTypeId: String, serviceId: String, trx: Configuration): Mono<ServiceSampleTypeRecord> {
        return trx.dsl().selectServiceSampleTypeById(sampleTypeId, serviceId)
            .switchIfEmpty(Mono.error(ServiceSampleTypeNotFoundException(sampleTypeId, serviceId)))
    }

    fun insertSampleAndExtensions(userId: String, orderId: UUID, serviceId: String, sample: Sample,
                                  patientSerial: String, organizationId: String, barcode: String, status: String, trx: Configuration
    ): Mono<Void> {
        return trx.dsl().insertSample(userId, orderId, serviceId, sample, patientSerial, organizationId, barcode, status)
            .flatMap { sampleRecord ->
                Flux.fromIterable(sample.extensions ?: emptyList())
                    .flatMap { extension -> trx.dsl().insertSampleExtension(extension, sampleRecord.id!!) }
                    .then()
            }
    }
}