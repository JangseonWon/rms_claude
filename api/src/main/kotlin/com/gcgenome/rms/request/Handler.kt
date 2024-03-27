package com.gcgenome.rms.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.*
import com.gcgenome.rms.exceptions.*
import org.jooq.Condition
import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.impl.DSL.field
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.reflect.full.declaredMemberProperties

@Service("com.gcgenome.rms.order.Handler")
class Handler(
    val dslContext: DSLContext
): PatientDao, OrderDao, ItemDao, SampleExtensionDao, SampleDao, UserDao, UserServiceDao {

    fun findOrder(barcode: String, userId: String): Mono<Sample> {
        return Mono.from(dslContext.transactionPublisher { trx->
            trx.dsl().run {
                selectSampleByBarcode(barcode).switchIfEmpty(Mono.error(SampleNotFoundException(barcode)))
                    .flatMap { selectSampleById(it.id!!) }
            }
        })
    }
    fun searchOrderList(condition:SampleSearchCondition): Flux<Sample>{
        val createDateFrom = condition.filters.createAtFrom.atStartOfDay()
        val createDateTo = condition.filters.createAtTo.atTime(LocalTime.MAX)
        if (ChronoUnit.DAYS.between(createDateFrom,createDateTo) > 6)
            throw MaxAllowedDaysException()
        val whereClause = buildSelectSampleWhereClause(condition.filters, createDateFrom,createDateTo)

        return Flux.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                selectSampleByCondition(condition,whereClause)
            }
        })
    }
    private fun buildSelectSampleWhereClause(filter:SampleSearchCondition.Companion.Filter, createDateFrom: LocalDateTime, createDateTo: LocalDateTime): Condition {
        var whereClause: Condition = field("create_at").between(createDateFrom, createDateTo)

        filter::class.declaredMemberProperties.forEach{ p ->
            val key = p.name
            val value = p.getter.call(filter)

            if(value != null) {
                when(key) {
                    "barcode", "serial"  -> {
                        whereClause = whereClause.and(field("SAMPLE.$key").like("%$value%"))
                    }
                    "sampleTypeId", "empId", "empName" -> {
                        val jsonAnnotation = p.annotations.find { it is JsonProperty } as? JsonProperty
                        val jsonName = jsonAnnotation?.value ?: key
                        whereClause = whereClause.and(field("SAMPLE.$jsonName").like("%$value%"))
                    }
                    "status" -> {
                        whereClause = whereClause.and(field(key).eq(value.toString()))
                    }
                    "test" -> {
                        whereClause = whereClause.and(field("labs_test").eq(value))
                    }
                }
            }
        }

        return whereClause
    }
    fun cancelOrder(barcode: String): Mono<Any> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                selectSampleByBarcode(barcode).switchIfEmpty(Mono.error(SampleNotFoundException(barcode)))
                    .filter{ it.status!! == Status.ORDERED.toString() }.switchIfEmpty(Mono.error(SampleDeleteException()))
                    .flatMap { removeSample(it, trx) }
            }
        })
    }
    private fun removeSample(sample: Sample, trx: Configuration): Mono<Any> {
        return Mono.from(trx.dsl().run {
            deleteSampleExtensionBySampleId(sample.id!!)
                .then(deleteSampleById(sample.id!!))
                .flatMap {
                    deletePatientById(it.patientSerial!!, it.organizationId!!, it.userId!!)
                        .then(deleteItemById(it.orderId!!,it.serviceId!!))
                        .flatMap { deleteOrderById(it.orderId!!) }
                }
        })
    }
}