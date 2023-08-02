package com.gcgenome.rms.request

import com.gcgenome.rms.tables.references.ITEM
import com.gcgenome.rms.tables.references.SAMPLE
import com.gcgenome.rms.data.Body
import com.gcgenome.rms.data.XMLQuery
import com.gcgenome.rms.service.DefaultRequestDao
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


@Service
class RequestHandler(
    private val dslContext: DSLContext
) {
    fun resolve(query: Mono<XMLQuery>, version: String):Mono<Body> {
       return predicate(query).flatMapMany { where ->
           DefaultRequestDao(dslContext).selectRequestWithWhere(where)
        }.collectList().map(::Body)
    }

    private fun predicate(query: Mono<XMLQuery>): Mono<Condition> {
        return query.map {
            val conditions = mutableListOf<Condition>()
            if(it.search?.reqNo != null)     predicate("request", it.search.reqNo)?.let { it1 -> conditions.add(it1) }
            if(it.search?.startDate != null) predicate("date from", it.search.startDate)?.let { it1 -> conditions.add(it1) }
            if(it.search?.endDate != null)   predicate("date to", it.search.endDate)?.let { it1 -> conditions.add(it1) }
            if(it.search?.itemCd != null)    predicate("service", it.search.itemCd)?.let { it1 -> conditions.add(it1) }
            if(it.search?.cstCd != null)     predicate("organization", it.search.cstCd)?.let { it1 -> conditions.add(it1) }
            DSL.and(conditions)
        }
    }
    private fun predicate(key: String?, value: String?): Condition? {
        return when {
            "request".contentEquals(key, ignoreCase = true) -> {
                if (value != null) {
                    SAMPLE.GENOME_BARCODE.eq(value)
                } else {
                    null
                }
            }
            "service".contentEquals(key, ignoreCase = true) -> {
                if (value != null) {
                    val services = value.split(",")
                    ITEM.SERVICE_ID.`in`(services)
                } else {
                    null
                }
            }
            "date from".contentEquals(key, ignoreCase = true) -> {
                if (value != null) {
                    val date = value.replace("-", "").replace("/", "").replace(" ", "")
                    SAMPLE.CREATE_AT.greaterOrEqual(toLocalDate(date)?.atStartOfDay())
                } else {
                    null
                }
            }
            "date to".contentEquals(key, ignoreCase = true) -> {
                if (value != null) {
                    val date = value.replace("-", "").replace("/", "").replace(" ", "")
                    SAMPLE.CREATE_AT.lessOrEqual(toLocalDate(date)?.plusDays(1)?.atStartOfDay())
                } else {
                    null
                }
            }
            "organization".contentEquals(key, ignoreCase = true) -> {
                if (value != null) {
                    val organizationIds = value.split(",")
                    SAMPLE.ORGANIZATION_ID.`in`(organizationIds)
                } else {
                    null
                }
            }
            else -> null
        }
    }
    private val fmtDate = DateTimeFormatter.ofPattern("yyyyMMdd")
    private val fmtDate2 = DateTimeFormatter.ofPattern("MMddyyyy")
    private fun toLocalDate(date: String): LocalDate? {
        return try {
            LocalDate.parse(date, fmtDate)
        } catch (e: DateTimeParseException) { try {
            LocalDate.parse(date, fmtDate2)
        } catch (e: DateTimeParseException) { null } }
    }
}