package com.gcgenome.rms.catalog.request

import com.gcgenome.rms.authentication.User
import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.Page
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.RequestDTO
import com.gcgenome.rms.data.Role
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class RequestHandler(val dslContext: DSLContext):
    RequestDao, RequestGroupDao, PatientDao, SampleDao, SampleExtensionDao
{
    fun saveRequest(user: User, requests: Array<RequestDTO>): Flux<RequestDTO> {
        return Flux.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                Flux.fromArray(requests).flatMap { request ->
                    insertPatient(user.id!!, request.sample!!.patient!!)
                        .then(insertSample(user, request.sample!!, request.status!!))
                        .flatMap { sampleRecord ->
                            val extensions = request.sample!!.extensions ?: emptyList()
                            Flux.fromIterable(extensions)
                                .filter { it.value != null }
                                .flatMap { extension -> insertSampleExtension(extension, sampleRecord.id!!) }
                                .then(
                                    if (request.requestGroup!!.id == null) { insertRequestGroup() }
                                    else { Mono.just(request.requestGroup!!) }
                                )
                                .flatMap { requestGroup ->
                                    val updatedRequest = request.apply {
                                        this.sample!!.id = sampleRecord.id
                                        this.user!!.id = user.id
                                        this.requestGroup!!.id = requestGroup.id
                                    }
                                    insertRequest(updatedRequest)
                                }
                        }
                }
            }
        })
    }

    fun searchRequests(authentication: UserAuthentication, query: Query):  Mono<Page<RequestDTO>> {
        authentication.takeIf { it.user.role == Role.USER.toString() }?.let {
            query.filterGroups = query.filterGroups ?: mutableListOf()
            query.filterGroups?.add(
                Query.FilterGroup(
                    filters = listOf(
                        Query.FilterGroup.Filter(
                            table = "request",
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

}