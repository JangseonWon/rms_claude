package com.gcgenome.rms.dashboard

import com.gcgenome.rms.authentication.User
import com.gcgenome.rms.dao.RequestDao
import com.gcgenome.rms.data.Page
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.Request
import com.gcgenome.rms.data.StatusCount
import org.jooq.DSLContext
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class Handler(
    val dslContext: DSLContext
): RequestDao {
    fun selectStatus(user : User) : Mono<StatusCount> {
        return dslContext.selectStatusCount(user)
    }
    fun selectRequests(user: User, query: Query): Mono<Pair<List<Request>, Page>> {
        return dslContext.selectRequestCountByUserId(user)
            .flatMap { count ->
                val adjustedQuery = query.apply { page.number -= 1 }
                val requests = dslContext.selectRequestByUserId(user, adjustedQuery)
                val totalPages = Mono.just((count + query.page.size - 1) / query.page.size)
                Mono.zip(requests.collectList(), totalPages) { request, pageCount ->
                    Pair(request, Page(query.page.size, query.page.number + 1, pageCount, count))
                }
            }
    }
}