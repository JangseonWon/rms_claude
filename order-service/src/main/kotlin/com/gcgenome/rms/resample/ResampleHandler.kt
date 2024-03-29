package com.gcgenome.rms.resample

import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.*
import com.gcgenome.rms.exceptions.RequestNotFoundException
import org.jooq.Configuration
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class ResampleHandler(
    val dslContext: DSLContext
) : OrderDao, RequestDao, SampleExtensionDao, SampleDao, UserDao {

    fun insertSampleRequest(userId: String, urlServiceId: String, orderId: UUID, sampleId: UUID, requestDto: Request): Mono<Order> {
        val dto = Dto(orderId = orderId, sampleId = sampleId, userId = userId, serviceId = urlServiceId)
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                checkRequest(orderId, sampleId, urlServiceId, trx)
                    .then(insertSampleProcess(dto, requestDto, trx))
                    .flatMap { sample -> insertRequest(sample.id!!, dto, requestDto)}
                    .then(selectRequestBySampleId(orderId, sampleId))
            }
        })
    }

    fun checkRequest(orderId: UUID, sampleId: UUID, serviceId: String, trx: Configuration): Mono<Request> {
        return trx.dsl().selectRequestById(orderId, sampleId, serviceId)
            .switchIfEmpty(Mono.error(RequestNotFoundException()))
    }

    fun insertSampleProcess(dto: Dto, request: Request, trx: Configuration): Mono<Sample> {
        return trx.dsl().run{
            selectUserById(dto.userId!!)
            .flatMap { userRecord ->
                generateSampleBarcode(userRecord.branchSerial, trx)
                    .flatMap { newBarcode ->
                        insertSample(newBarcode, request.sample!!)
                            .flatMap { sample ->
                                insertSampleExtensionProcess(sample.id!!, request.sample.extensions, trx)
                                    .then(selectSampleById(sample.id!!))
                            }
                    }
            }}
    }

    fun insertSampleExtensionProcess(sampleId: UUID, extensions: List<Extension>?, trx: Configuration): Mono<Void> {
        return trx.dsl().run {
            Flux.fromIterable(extensions ?: emptyList())
                .flatMap { extension -> insertSampleExtension(extension, sampleId) }
                .then()
        }
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

}