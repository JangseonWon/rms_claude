package com.gcgenome.rms.service

import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.Item
import com.gcgenome.rms.data.Sample
import com.gcgenome.rms.data.Status
import com.gcgenome.rms.exceptions.OrganizationNotFoundException
import com.gcgenome.rms.exceptions.ServiceNotFoundException
import com.gcgenome.rms.exceptions.ServiceSampleTypeNotFoundException
import com.gcgenome.rms.tables.records.OrderRecord
import com.gcgenome.rms.tables.records.OrganizationRecord
import com.gcgenome.rms.tables.records.ServiceSampleTypeRecord
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
                    .thenMany(insertSampleProcess(userId, orderRecord, items, Status.ORDERED, trx))
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

    fun insertSampleAndExtensions(userId: String, orderId: UUID, item: Item, sample: Sample, barcode: String, status: String, trx: Configuration): Mono<Void> {
        return trx.dsl().insertSample(userId, orderId, item.serviceId, sample,
            item.patient.serial, item.patient.organization.id, barcode, status)
            .flatMap { sampleRecord ->
                Flux.fromIterable(sample.extensions ?: emptyList())
                    .flatMap { extension -> trx.dsl().insertSampleExtension(extension, sampleRecord.id!!) }
                    .then()
            }
    }

    fun insertSampleProcess(userId: String, orderRecord: OrderRecord, items: Item, status: Status,trx: Configuration): Flux<Void> {
        return Flux.fromIterable(items.patient.samples)
            .concatMap { sample ->
                checkServiceSampleTypeById(sample.sampleTypeId!!, items.serviceId, trx)
                    .then(trx.dsl().selectUserById(userId))
                    .flatMap { userRecord ->
                        generateSampleBarcode(userRecord.branchSerial, trx)
                            .flatMap { barcode ->
                                if (status == Status.CART) sample.apply { cartAt = LocalDateTime.now() }
                                insertSampleAndExtensions(userRecord.id, orderRecord.id!!, items,
                                    sample, barcode, status.toString(), trx)
                            }
                    }
            }
    }
}