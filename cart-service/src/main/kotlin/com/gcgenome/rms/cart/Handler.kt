package com.gcgenome.rms.cart

import com.gcgenome.rms.authentication.User
import com.gcgenome.rms.dao.RequestDao
import com.gcgenome.rms.data.*
import com.gcgenome.rms.exceptions.OrderNotFoundException
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

@Component
class Handler(val dslContext: DSLContext ): RequestDao {

    fun getCartInfo(orderId: UUID, sampleId: UUID, serviceId: String): Mono<Request> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                selectRequestById(orderId, sampleId, serviceId)
                .switchIfEmpty(Mono.error(OrderNotFoundException()))
            }
        })
    }
    fun requests(user: User, query: Query): Mono<Pair<List<Request>, Page>> {
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