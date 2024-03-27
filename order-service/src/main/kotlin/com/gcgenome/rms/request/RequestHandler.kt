package com.gcgenome.rms.request

import com.gcgenome.rms.dao.RequestDao
import com.gcgenome.rms.data.Page
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.SelectRequest
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class RequestHandler(
    val dslContext: DSLContext
): RequestDao {
    fun pageCount(query: Query, totalCount: Int) : Int{
        var totalPage = totalCount / query.size
        if (totalCount % query.size != 0) totalPage++
        return totalPage
    }

    fun selectRequests(userId: String, query: Query) : Mono<Page<SelectRequest>> {
        val filters = query.filters ?: emptyList()
        val whereClause = buildSelectUserOrganizationWhereClause(filters)
        val request =  dslContext.selectRequests(query,whereClause, userId)
        return dslContext.selectRequestsCount(query, whereClause, userId)
            .flatMap { totalCount ->
                request.collectList().flatMap { list ->
                    val page = Page(totalCount, pageCount(query, totalCount), query.size, query.page + 1, list)
                    Mono.just(page)
                }
            }
    }

    fun buildSelectUserOrganizationWhereClause(filters: List<Query.Companion.Filter>): Condition {
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

        return if (conditions.isNotEmpty()) {
            conditions.reduceOrNull { acc, condition -> acc?.and(condition) ?: condition }
                ?: trueCondition()
        } else {
            trueCondition()
        }
    }
}