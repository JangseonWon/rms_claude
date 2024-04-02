package com.gcgenome.rms.request

import com.gcgenome.rms.authentication.User
import com.gcgenome.rms.dao.OrderDao
import com.gcgenome.rms.dao.RequestDao
import com.gcgenome.rms.data.Order
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
import java.util.UUID

@Service
class RequestHandler(
    val dslContext: DSLContext
): RequestDao, OrderDao {
    fun pageCount(query: Query, totalCount: Int) : Int{
        var totalPage = totalCount / query.size
        if (totalCount % query.size != 0) totalPage++
        return totalPage
    }

    fun selectRequest(orderId: UUID, sampleId: UUID, serviceId: String) : Mono<Order> {
        return dslContext.selectRequestBySampleId(orderId, sampleId, serviceId)
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
            acc?.or(condition) ?: condition
        } ?: trueCondition()

        return andCondition.and(orCondition)
    }
}