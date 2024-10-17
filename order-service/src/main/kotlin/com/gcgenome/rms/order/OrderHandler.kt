package com.gcgenome.rms.order

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.dao.RequestDao
import com.gcgenome.rms.data.Page
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.Request
import com.gcgenome.rms.data.RequestDTO
import com.gcgenome.rms.exception.RequestNotFoundException
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

@Component
class OrderHandler(
    val dslContext: DSLContext
): RequestDao {
    fun selectRequestsStatusOrder(user: UserAuthentication, query: Query):  Mono<Page<RequestDTO>> {
        return dslContext.selectOrderStatusRequestsWithPage(query, user.user)
    }

    fun checkRequest(orderId: UUID, sampleId: UUID, serviceId: String) : Mono<Request> {
        return dslContext.selectRequestByPK(orderId, sampleId,  serviceId)
            .switchIfEmpty(Mono.error(RequestNotFoundException()))
    }
}