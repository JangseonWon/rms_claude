package com.gcgenome.rms.resample

import com.gcgenome.rms.authentication.User
import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.*
import com.gcgenome.rms.exception.OrderNotFoundException
import com.gcgenome.rms.exception.RequestForbiddenException
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
) : OrderDao, RequestDao, SampleExtensionDao, SampleDao, UserDao, PatientDao {

    fun traceSample(orderId: UUID): Mono<Order> {
        return Mono.from(dslContext.selectOrderById(orderId))
    }

    fun insertSampleRequest(userDto: User, dto: Request): Mono<Request> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                checkRequest(dto.orderId!!, dto.serviceId!!, dto.sampleId!!, trx)
                    .then(insertSampleProcess(userDto, dto, trx))
                    .flatMap { sample -> insertRequest(dto.apply { sampleId = sample.id!! })}
                    .flatMap { request -> selectRequestByPK(request.orderId!!, request.sampleId!!, request.serviceId!!)}
            }
        })
    }

    fun cancelRequest(user: User, orderId: UUID, serviceId: String, sampleId: UUID): Mono<Any> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                checkRequest(orderId, serviceId, sampleId, trx)
                    .flatMap { request ->
                        checkUserStatusAndRole(user, request.status!!)
                            .then(deleteRequestById(orderId, sampleId, serviceId))
                            .then(deleteSampleExtensionBySampleId(sampleId))
                            .then(deleteSampleById(sampleId))
                            .flatMap {deletePatientById(request.patient!!, user.id) }
                            .then(deleteOrderById(orderId))
                    }
            }
        })
    }

    fun checkUserStatusAndRole(user: User, status: String?) : Mono<Boolean> {
        return if ((user.role.equals("USER") && status == Status.ORDERED.toString()) ||
            (user.role.equals("USER") && status == Status.CART.toString()) ||
            !user.role.equals("USER")) { Mono.just(true) }
        else {
            Mono.error(RequestForbiddenException())
        }
    }


    fun checkRequest(orderId: UUID, serviceId: String, sampleId: UUID, trx: Configuration): Mono<Request> {
        return trx.dsl().selectRequestById(orderId, serviceId, sampleId)
            .switchIfEmpty(Mono.error(OrderNotFoundException()))
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