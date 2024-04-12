package com.gcgenome.rms.request

import com.gcgenome.rms.authentication.User
import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.*
import com.gcgenome.rms.exception.RequestForbiddenException
import com.gcgenome.rms.exception.RequestNotFoundException
import com.gcgenome.rms.exception.WebInputException
import com.gcgenome.rms.tables.records.SampleExtensionRecord
import org.jooq.Condition
import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

@Service
class RequestHandler(
    val dslContext: DSLContext
): RequestDao, OrderDao, PatientDao, SampleDao, SampleExtensionDao {
    fun pageCount(query: Query, totalCount: Int) : Int{
        var totalPage = totalCount / query.size
        if (totalCount % query.size != 0) totalPage++
        return totalPage
    }

    fun checkRequest(orderId: UUID, sampleId: UUID, serviceId: String) : Mono<Request> {
        return dslContext.selectRequestByPK(orderId, sampleId,  serviceId)
            .switchIfEmpty(Mono.error(RequestNotFoundException()))
    }

    fun selectRequests(userDto: User, status: Boolean, query: Query) : Mono<Page<SelectRequest>> {
        val filters = query.filters ?: emptyList()
        val where = buildFilterWhereClause(filters, status)
        val request =  dslContext.selectRequests(query, where, userDto)
        return dslContext.selectRequestsCount(query, where, userDto)
            .flatMap { totalCount ->
                request.collectList().flatMap { list ->
                    val page = Page(totalCount, pageCount(query, totalCount), query.size, query.page + 1, list)
                    Mono.just(page)
                }
            }
    }

    fun checkUserStatusAndRole(user: User, status: String?) : Mono<Boolean> {
        return if ((user.role.equals("USER") && status == Status.ORDERED.toString()) ||
            (user.role.equals("USER") && status == Status.CART.toString()) ||
            !user.role.equals("USER")) { Mono.just(true) }
        else {
            Mono.error(RequestForbiddenException())
        }
    }

    fun checkStatus(status: String?) : Mono<Boolean> {
        return if (status != null && Status.entries.map { it.name }.contains(status.uppercase())) {
            Mono.just(true)
        }
        else {
            Mono.error(WebInputException())
        }
    }

    fun isValidDate(patient: Patient): Mono<Boolean> {
        return try {
            val year = patient.birthYear!!.toInt()
            val month = patient.birthMonth!!.toInt()
            val day = patient.birthDay!!.toInt()

            val isValidMonth = month in 1..12
            val dayOfMonth = getMaxDayOfMonth(year, month)

            if (dayOfMonth == 0)
                Mono.just(false)

            val isValidDay = day in 1..dayOfMonth
            if (isValidMonth && isValidDay) Mono.just(true)
            else  Mono.error(WebInputException())
        } catch (e: Exception) {
            Mono.error(WebInputException())
        }
    }

    private fun getMaxDayOfMonth(year: Int, month: Int): Int {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
            else -> 0
        }
    }

    fun patientUpdateProcess(userId: String, patient: Patient, trx: Configuration): Mono<Patient> {
        return trx.dsl().run {
            Mono.from(isValidDate(patient))
                .then(insertPatient(patient, userId))
                .then(updateSample(patient.sample!!,patient.organization!!.id))
                .then(deletePatientById(patient, userId))
        }
    }

    fun updateSampleExtensionProcess(sample: Sample, trx: Configuration) : Flux<SampleExtensionRecord>{
        return trx.dsl().run {
            Flux.from(deleteSampleExtensionBySampleId(sample.id!!))
                .thenMany(
                    Flux.fromIterable(sample.extensions ?: emptyList())
                        .flatMap { extension -> insertSampleExtension(extension, sample.id!!) })
        }
    }

    fun updateRequest(request: Request, trx: Configuration) : Mono<Request>{
        return trx.dsl().run {
            checkStatus(request.status.toString())
                .then(updateRequest(request))
        }
    }

    fun updateRequestProcess(user: User, request: Request): Mono<Request> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                checkRequest(request.orderId!!, request.sampleId!!, request.serviceId!!)
                    .flatMap { req ->
                        checkUserStatusAndRole(user, req.status)
                            .then(patientUpdateProcess(user.id!!, request.patient!!, trx))
                            .then(updateSampleExtensionProcess(request.patient.sample!!, trx)
                            .then(updateRequest(request, trx)))
                }
            }
        })
    }

    fun buildFilterWhereClause(filters: List<Query.Companion.Filter>, status: Boolean): Condition {
        var conditions : List<Condition?> = mutableListOf()
        conditions = filters.map { filter ->
            var key = filter.key
            val value = filter.value
            key?.let {
                value?.takeIf { it.isNotBlank() }?.let {
                    if (!key.equals("createFrom") && !key.equals("createTo")) {
                        if (key.equals("serial") || key.equals("name"))
                            key = "PATIENT." + key
                        field(key).like("%$it%") as Condition?
                    }
                    else {
                        null
                    }
                }
            }
        }

        val fromDate = filters.find { it.key.equals( "createFrom") }?.value
        val toDate = filters.find { it.key.equals("createTo") }?.value

        if (fromDate != null && toDate != null) {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val startDate = LocalDate.parse(fromDate, formatter).atStartOfDay()
            val endDate = LocalDate.parse(toDate, formatter).plusDays(1).atStartOfDay()
            val betweenCondition = field("REQUEST.create_at").between(startDate, endDate)
            (conditions as MutableList).add(betweenCondition)
        }

        val progressCondition: List<Condition?> = mutableListOf()
        if (status) {
            val progress = arrayOf("ORDERED", "SPECIFIED", "INPROGRESS", "TESTFALIED", "DELIVERED")
            progress.forEach { i -> (progressCondition as MutableList).add(field("status").like("%$i%")) }
        }

        val andCondition = conditions.reduceOrNull { acc, condition ->
            acc?.and(condition) ?: condition
        } ?: trueCondition()

        val orCondition = progressCondition.filterNotNull().reduceOrNull { acc, condition ->
            acc.or(condition) ?: condition
        } ?: trueCondition()

        return andCondition.and(orCondition)
    }
}