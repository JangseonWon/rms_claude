package com.gcgenome.rms.request

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.dao.RequestDao
import com.gcgenome.rms.dao.SampleDao
import com.gcgenome.rms.dao.SampleExtensionDao
import com.gcgenome.rms.data.*
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Component
class RequestHandler(
    val dslContext: DSLContext
): RequestDao, SampleDao, SampleExtensionDao {
    fun getRequestById(sampleId: UUID, serviceId: String): Mono<RequestDTO> {
        return dslContext.selectRequestById(sampleId, serviceId)
    }
     fun saveResampleRequest(request: RequestDTO, rootServiceId: String, rootSampleId: UUID): Mono<Void> {
        return Mono.from(
            dslContext.transactionPublisher { transaction ->
                val dsl = transaction.dsl()
                dsl.insertSample(request.user!!, request.sample!!, request.status!!)
                    .flatMap { sampleDto ->
                        request.sample!!.id = sampleDto.id
                        dsl.insertResampleRequest(request)
                            .thenMany(saveSampleExtensions(dsl, sampleDto.id!!, request.sample!!.extensions ?: emptyList()))
                            .then(dsl.updateRequestStatusToComplete(RequestDTO(service = ServiceDTO(id = rootServiceId), sample = SampleDTO(id=rootSampleId))))
                    }.then()
        })
    }
    fun selectRequests(authentication: UserAuthentication, query: Query):  Mono<Page<RequestDTO>> {
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

    fun updateRequests(requests: List<RequestDTO>): Mono<Void> {
        return Mono.from(dslContext.transactionPublisher { transaction ->
            transaction.dsl().run {
                Flux.fromIterable(requests)
                    .flatMap { request ->
                        updateRequestStatusToComplete(request)
                    }.then()
            }
        })
    }

    fun saveSampleExtensions(dsl: DSLContext, sampleId: UUID, extensions: List<ExtensionDTO>): Flux<ExtensionDTO>{
        return Flux.fromIterable(extensions)
            .filter{ extension -> extension.value != null}
            .flatMap { extension ->
                dsl.insertSampleExtension(SampleExtensionDTO(sampleId = sampleId, extensionId = extension.id, value = extension.value))
            }
    }
}