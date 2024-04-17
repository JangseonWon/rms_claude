package com.gcgenome.rms.order

import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.*
import com.gcgenome.rms.exceptions.*
import com.gcgenome.rms.tables.references.ORDER
import com.gcgenome.rms.tables.references.REQUEST
import com.gcgenome.rms.tables.references.SAMPLE
import org.jooq.Condition
import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*


@Service("com.gcgenome.rms.order.Handler")
class Handler(
    val dslContext: DSLContext
): PatientDao, OrderDao, RequestDao, SampleExtensionDao, SampleDao, UserDao, UserServiceDao, OrganizationDao, ServiceSampleTypeDao {

    fun addOrders(user: User, orders:Array<Order>) : Flux<Order> {
        return Flux.from(dslContext.transactionPublisher{ trx ->
            trx.dsl().run {
                Flux.fromArray(orders)
                    .flatMap { order ->
                        addOrder(user.id,trx)
                            .flatMap{ newOrder ->
                                Flux.fromArray(order.requests!!)
                                    .concatMap { request ->
                                        selectUserServiceById(user.id,request.service!!.id).switchIfEmpty(Mono.error(ServiceNotFoundException(request.service.id)))
                                            .flatMap { selectServiceSampleTypeById(it.serviceId,request.sample!!.sampleType!!.id).switchIfEmpty(Mono.error(ServiceSampleTypeNotFoundException(it.serviceId,request.sample.sampleType!!.id))) }
                                            .then(insertOrganization(user.id,request.sample!!.patient!!.organization!!))
                                            .then(insertPatient(user.id,request.sample.patient!!))
                                            .flatMap { addSample(user,request.sample,trx) }
                                            .flatMap { addSampleExtension(it.id!!, request.sample.extensions!!, trx)
                                                .then(insertRequest(newOrder.id!!, request, it.id!!))
                                            }
                                    }
                                    .then(Mono.just(newOrder))
                            }
                    .flatMap { selectOrderById(it.id!!) }
                    }
            }
        })
    }

    fun addOrder(userId: String,trx: Configuration): Mono<Order> {
        return Mono.from(trx.dsl().run {
            generateOrderSerial(userId,trx)
                .flatMap { trx.dsl().insertOrder(userId,it) }
        })
    }

    fun addSample(user: User, sample: Sample, trx: Configuration) : Mono<Sample> {
        val whereClause: Condition = SAMPLE.USER_SAMPLE_ID.eq(sample.userSampleId)
            .and(SAMPLE.USER_ID.eq(user.id))
            .and(SAMPLE.SAMPLE_TYPE_ID.eq(sample.sampleType!!.id))
            .and(SAMPLE.PATIENT_SERIAL.eq(sample.patient!!.serial))
            .and(SAMPLE.ORGANIZATION_ID.eq(sample.patient.organization!!.id))

        return Mono.from(trx.dsl().run {
            selectSampleByCondition(whereClause)
                .switchIfEmpty {
                    generateSampleBarcode(user.branchSerial!!,trx)
                        .flatMap { insertSample(user.id,sample,it) }
                }
        })
    }

    fun addSampleExtension(sampleId: UUID, extensions: List<Extension>, trx: Configuration): Flux<SampleExtension> {
        return Flux.from(
            trx.dsl().run {
                Flux.fromIterable(extensions)
                    .flatMap { extension ->
                        insertSampleExtensionBySampleId(sampleId,extension) }
            }
        )
    }

    fun generateOrderSerial(userId: String, trx: Configuration): Mono<String> {
        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val serialPrefix = "$userId$currentDate"
        return trx.dsl().selectOrderSerial(serialPrefix)
            .map { "${serialPrefix}${"%4d".format(it.takeLast(4).toLong() + 1)}" }
            .switchIfEmpty(Mono.just("${serialPrefix}5001"))
    }

    fun generateSampleBarcode(branchSerial:String,trx: Configuration): Mono<String> {
        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val barcodePrefix = "$currentDate$branchSerial"
        return trx.dsl().selectSampleBarcode(barcodePrefix)
            .map { "${barcodePrefix}${"%4d".format(it.takeLast(4).toLong() + 1)}" }
            .switchIfEmpty { Mono.just("${barcodePrefix}5001") }
    }

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
                service?.let { whereClause = addLikeCondition(whereClause, "request.service_id", it.id) }
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
                    sampleType?.let { whereClause = addLikeCondition(whereClause, "sample.sample_type_id", it.id) }
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

    fun cancelOrder(userId: String, serial:String, serviceId: String, barcode: String): Mono<Any> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                selectOrderBySerial(userId,serial).switchIfEmpty(Mono.error(OrderNotFoundException(serial)))
                    .zipWith(selectSampleByBarcode(barcode).switchIfEmpty(Mono.error(SampleNotFoundException(barcode))))
                    .flatMap { selectRequestById(it.t1.id!!, serviceId,it.t2.id!!).switchIfEmpty(Mono.error(RequestNotFoundException(serial,serviceId,barcode))) }
                    .filter { it.status!! == Status.ORDERED.toString() }.switchIfEmpty(Mono.error(SampleDeleteException()))
                    .flatMap { removeRequest(it, trx) }
                    .flatMap { request ->
                        removeSample(request.sampleId!!, trx)
                            .flatMap {
                                removeOrder(request.orderId!!,trx)
                                    .then(removePatient(Patient(it.organizationId!!, it.patientSerial!!, it.userId!!),trx))
                            }
                    }
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