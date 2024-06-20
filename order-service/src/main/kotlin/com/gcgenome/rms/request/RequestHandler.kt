package com.gcgenome.rms.request

import com.gcgenome.rms.authentication.User
import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.*
import com.gcgenome.rms.exception.RequestForbiddenException
import com.gcgenome.rms.exception.RequestNotFoundException
import com.gcgenome.rms.exception.WebInputException
import com.gcgenome.rms.tables.records.SampleExtensionRecord
import com.gcgenome.rms.tables.references.PATIENT
import com.gcgenome.rms.tables.references.REQUEST
import com.gcgenome.rms.tables.references.SAMPLE
import org.jooq.Condition
import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.util.*

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

    fun selectRequests(userDto: User, status: String, query: Query) : Mono<Page<Request>> {
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

    fun sampleUpdateProcess(userId: String, sample: Sample, trx: Configuration): Mono<Patient> {
        return trx.dsl().run {
            Mono.from(isValidDate(sample.patient!!))
                .then(insertPatient(sample.patient, userId))
                .then(updateSample(sample,sample.patient.organization!!.id))
                .then(deletePatientById(sample.patient, userId))
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
                            .then(sampleUpdateProcess(user.id!!, request.sample!!, trx))
                            .then(updateSampleExtensionProcess(request.sample, trx)
                            .then(updateRequest(request, trx)))
                }
            }
        })
    }

    fun buildFilterWhereClause(filters: List<Query.Companion.Filter>, status: String): Condition {
        var conditions: Condition = noCondition()
        filters.let {
            for (filter in it) {
                conditions = when (filter.key) {
                    "date_from" -> conditions.and(REQUEST.CREATE_AT.ge(LocalDate.parse(filter.value).atStartOfDay()))
                    "date_to" -> conditions.and(REQUEST.CREATE_AT.le(LocalDate.parse(filter.value).plusDays(1).atStartOfDay()))
                    "status" -> conditions.and(REQUEST.STATUS.eq(filter.value))
                    "search" -> {
                        conditions.and(SAMPLE.BARCODE.likeIgnoreCase("%${filter.value}%"))
                            .or(PATIENT.NAME.likeIgnoreCase("%${filter.value}%"))
                            .or(PATIENT.SERIAL.likeIgnoreCase("%${filter.value}%"))
                            .or(REQUEST.PHYSICIAN.likeIgnoreCase("%${filter.value}%"))
                            .or(PATIENT.ORGANIZATION_ID.likeIgnoreCase("%${filter.value}%"))
                    }
                    else -> conditions
                }
            }
        }

        val progressCondition: List<Condition?> = mutableListOf()
        when {
            status == "resample" -> {
                (progressCondition as MutableList).add(field("resample_at").isNotNull)
            }
            status == "inprogress" -> {
                val progress = Status.entries.filter { it != Status.CART && it != Status.FINISHED }
                progress.forEach { status ->
                    (progressCondition as MutableList).add(field("status").like("%$status%"))
                }
            }
            status == "result" -> {
                val progress = Status.entries.filter { it == Status.DELIVERED }
                progress.forEach { status ->
                    (progressCondition as MutableList).add(field("status").like("%$status%"))
                }
            }
            status == "confirm" -> {
                val progress = Status.entries.filter { it == Status.ORDERED }
                progress.forEach { status ->
                    (progressCondition as MutableList).add(field("status").like("%$status%"))
                }
            }
            status == "order" -> {
                val progress = Status.entries.filter { it == Status.ORDERED || it == Status.INPROGRESS }
                progress.forEach { status ->
                    (progressCondition as MutableList).add(field("status").like("%$status%"))
                }
            }
            status == "deliver" -> {
                val progress = Status.entries.filter { it == Status.DELIVERED }
                progress.forEach { status ->
                    (progressCondition as MutableList).add(field("status").like("%$status%"))
                }
            }
            status == "download" -> {
                val progress = Status.entries.filter { it == Status.DELIVERED || it == Status.FINISHED}
                progress.forEach { status ->
                    (progressCondition as MutableList).add(field("status").like("%$status%"))
                }
            }
            else -> {
                if (status != "all")
                    throw WebInputException()
            }
        }

        val orCondition = progressCondition.filterNotNull().reduceOrNull { acc, condition ->
            acc.or(condition)
        } ?: trueCondition()

        return conditions.and(orCondition)
    }
}