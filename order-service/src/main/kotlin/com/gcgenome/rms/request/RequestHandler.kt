package com.gcgenome.rms.request

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.dao.RequestDao
import com.gcgenome.rms.dao.ServiceDao
import com.gcgenome.rms.data.Page
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.RequestDTO
import com.gcgenome.rms.data.ServiceDTO
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class RequestHandler(
    val dslContext: DSLContext
): RequestDao, ServiceDao {
    fun selectRequests(user: UserAuthentication, query: Query):  Mono<Page<RequestDTO>> {
        return dslContext.selectRequestsWithPage(query, user.user)
    }
    fun selectService(serviceId: String): Flux<ServiceDTO> {
        return Flux.from(
            dslContext.selectServiceById(serviceId)
                .flatMapMany { service ->
                    if (service.type == "GENERAL") {
                        Flux.empty()
                    } else {
                        dslContext.selectServiceForGroupServiceId(service.groupName!!)
                    }
                }.flatMapSequential { relatedService ->
                    dslContext.selectServiceInfoById(relatedService.id!!)
                }
        )
    }
}