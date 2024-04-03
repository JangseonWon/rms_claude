package com.gcgenome.rms.resample

import com.gcgenome.rms.authentication.User
import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.*
import com.gcgenome.rms.exceptions.RequestNotFoundException
import com.gcgenome.rms.tables.records.SampleExtensionRecord
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

    fun traceSample(orderId: UUID): Mono<Order> {
        return Mono.from(dslContext.selectOrderById(orderId))
    }

    fun insertSampleRequest(userDto: User, dto: Request): Mono<Request> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                selectRequest(dto, trx)
                    .then(insertSampleProcess(userDto, dto, trx))
                    .flatMap { sample -> insertRequest(dto.apply { sampleId = sample.id!! })}
                    .flatMap { request -> selectRequestByPK(request.orderId!!, request.sampleId!!, request.serviceId!!)}
            }
        })
    }

    fun selectRequest(dto:Request, trx: Configuration): Mono<Request> {
        return trx.dsl().selectRequestById(dto)
            .switchIfEmpty(Mono.error(RequestNotFoundException()))
    }

    fun insertSampleProcess(userDto: User, requestDto: Request, trx: Configuration): Mono<Sample> {
        return trx.dsl().run{
                generateSampleBarcode(userDto.branchSerial!!, trx)
                    .flatMap { newBarcode ->
                        insertSample(newBarcode, requestDto.sample!!)
                            .flatMap { sample ->
                                insertSampleExtensionProcess(sample.id!!, requestDto.sample.extensions, trx)
                                    .then(selectSampleById(sample.id!!))
                            }

                }}
    }

    fun insertSampleExtensionProcess(sampleId: UUID, extensions: List<Extension>?, trx: Configuration): Flux<SampleExtensionRecord> {
        return trx.dsl().run {
            Flux.fromIterable(extensions ?: emptyList())
                .flatMap { extension -> insertSampleExtension(extension, sampleId) }
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