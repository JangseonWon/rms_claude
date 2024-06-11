package com.gcgenome.rms.dashboard

import com.gcgenome.rms.authentication.User
import com.gcgenome.rms.dao.RequestDao
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.Request
import com.gcgenome.rms.data.StatusCount
import com.gcgenome.rms.tables.references.PATIENT
import com.gcgenome.rms.tables.references.REQUEST
import com.gcgenome.rms.tables.references.SAMPLE
import com.gcgenome.rms.tables.references.SERVICE
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDate

@Service
class Handler(
    val dslContext: DSLContext
): RequestDao {
    fun selectStatus(user : User) : Mono<StatusCount> {
        return dslContext.selectStatusCount(user)
    }
    fun selectRequests(user: User, query: Query): Mono<Pair<List<Request>, Query.Page>> {
        val condition = createCondition(query)
        return dslContext.selectRequestCountByUserId(user, condition)
            .flatMap { count ->
                val adjustedQuery = query.apply { page.number -= 1 }
                val requests = dslContext.selectRequestByUserId(user, condition, adjustedQuery)
                val totalPages = Mono.just((count + query.page.size - 1) / query.page.size)
                Mono.zip(requests.collectList(), totalPages) { request, pageCount ->
                    Pair(request, Query.Page(query.page.size, query.page.number + 1, pageCount, count))
                }
            }
    }
    fun createCondition(query: Query): Condition {
        var condition: Condition = DSL.noCondition()
        query.filters?.let {
            for (filter in it) {
                condition = when (filter.field) {
                    "date_from" -> condition.and(REQUEST.CREATE_AT.ge(LocalDate.parse(filter.value).atStartOfDay()))
                    "date_to" -> condition.and(REQUEST.CREATE_AT.le(LocalDate.parse(filter.value).plusDays(1).atStartOfDay()))
                    "status" -> condition.and(REQUEST.STATUS.eq(filter.value))
                    "search" -> {
                        condition.and(SAMPLE.BARCODE.like("%${filter.value}%"))
                            .or(SERVICE.NAME.like("%${filter.value}%"))
                            .or(PATIENT.NAME.like("%${filter.value}%"))
                            .or(PATIENT.SERIAL.like("%${filter.value}%"))
                    }

                    else -> condition
                }
            }
        }
        return condition
    }
}