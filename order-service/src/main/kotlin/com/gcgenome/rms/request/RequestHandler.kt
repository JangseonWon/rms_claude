package com.gcgenome.rms.request

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.dao.RequestDao
import com.gcgenome.rms.data.*
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class RequestHandler(
    val dslContext: DSLContext
): RequestDao {
    fun selectRequests(user: UserAuthentication, query: Query):  Mono<Page<RequestDTO>> {
        return dslContext.selectRequestsWithPage(query, user.user)
    }
    fun updateRequests(requests: List<RequestDTO>):Mono<Void> {
        return Flux.fromIterable(requests)
            .flatMap { request -> dslContext.updateRequest(request) }.then()
    }
}