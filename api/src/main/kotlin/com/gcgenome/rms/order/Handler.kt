package com.gcgenome.rms.order

import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.*
import com.gcgenome.rms.exceptions.*
import com.gcgenome.rms.tables.references.ORDER
import com.gcgenome.rms.tables.references.REQUEST
import org.jooq.Condition
import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.*


@Service("com.gcgenome.rms.order.Handler")
class Handler(
    val dslContext: DSLContext
): PatientDao, OrderDao, RequestDao, SampleExtensionDao, SampleDao, UserDao, UserServiceDao {

    fun searchOrderList(userId: String, condition:SearchCondition): Flux<Order> {
        val whereClause = buildSelectOrderWhereClause(userId,condition.filter)

        return Flux.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                selectOrderByCondition(condition,whereClause)
            }
        })
    }


    fun buildSelectOrderWhereClause(userId: String,filter:Filter): Condition {
        val dateFrom = filter.request.dateFrom.atStartOfDay()
        val dateTo = filter.request.dateTo.atTime(LocalTime.MAX)
        if (ChronoUnit.DAYS.between(dateFrom,dateTo) > 6 || dateFrom.isAfter(dateTo))
            throw MaxAllowedDaysException()
        var whereClause: Condition = REQUEST.CREATE_AT.between(dateFrom, dateTo)
            .and(ORDER.USER_ID.eq(userId))

        with(filter) {
            with(order) {
                this?.let { serial?.let { whereClause = addLikeCondition(whereClause, "\"rms_dev\".\"order\".\"serial\"", it) } }
            }
            with(request) {
                serviceId?.let { whereClause = addLikeCondition(whereClause, "request.service_id", it) }
                userServiceId?.let { whereClause = addLikeCondition(whereClause, "request.user_service_id", it) }
                status?.let { whereClause = addEqCondition(whereClause, "request.status", it.toString()) }
                empId?.let { whereClause = addLikeCondition(whereClause, "request.emp_id", it) }
                empName?.let { whereClause = addLikeCondition(whereClause, "request.emp_name", it) }
                test?.let { whereClause = addEqCondition(whereClause, "request.test", it) }
            }
            with(sample) {
                this?.let {
                    barcode?.let { whereClause = addLikeCondition(whereClause, "sample.barcode", it) }
                    userSampleId?.let { whereClause = addLikeCondition(whereClause, "sample.user_sample_id", it) }
                    sampleTypeId?.let { whereClause = addLikeCondition(whereClause, "sample.sample_type_id", it) }
                }
            }
            with(patient) {
                this?.let {
                    patientSerial?.let { whereClause = addLikeCondition(whereClause, "patient.serial", it) }
                    name?.let { whereClause = addLikeCondition(whereClause, "patient.name", it) }
                    organizationId?.let { whereClause = addLikeCondition(whereClause, "patient.organization_id", it) }
                }
            }
        }
        return whereClause
    }

    private fun addLikeCondition(whereClause:Condition, field:String, value:Any):Condition{
        return whereClause.and(field(field).like("%$value%"))
    }

    private fun addEqCondition(whereClause: Condition, field: String, value: Any):Condition{
        return whereClause.and(field(field).eq(value))
    }

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