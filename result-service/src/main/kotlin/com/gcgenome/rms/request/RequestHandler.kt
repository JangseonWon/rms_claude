package com.gcgenome.rms.request

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.dao.RequestDao
import com.gcgenome.rms.data.*
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Component
class RequestHandler(
    val dslContext: DSLContext
): RequestDao {
    fun selectRequests(authentication: UserAuthentication, query: Query):  Mono<Page<RequestDTO>> {
        authentication.takeIf { it.user.role == Role.USER.toString() }?.let {
            query.filterGroups = query.filterGroups ?: mutableListOf()  // null 체크 및 초기화
            query.filterGroups?.add(
                Query.FilterGroup(
                    filters = listOf(
                        Query.FilterGroup.Filter(
                            table = "order",
                            column = "user_id",
                            operator = "=",
                            value = authentication.user.id!!
                        )
                    )
                )
            )
        }
        return dslContext.selectRequestsWithPage(query)
    }

    fun updateRequests(requests: List<RequestDTO>): Mono<Void> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                Flux.fromIterable(requests)
                    .flatMap { request ->
                        if(request.status == Status.COMPLETED) {
                            request.apply { completeAt = LocalDateTime.now() }
                        }
                        updateRequests(request)
                    }.then()
            }
        })
    }
}