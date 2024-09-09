package com.gcgenome.rms.product

import com.gcgenome.rms.authentication.User
import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.Organization
import com.gcgenome.rms.data.Request
import com.gcgenome.rms.data.SampleType
import com.gcgenome.rms.data.Service
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import java.util.*

@Component
class Handler(val dslContext: DSLContext):
    OrganizationDao, ServiceDao, SampleTypeDao, OrderDao, RequestDao, PatientDao, SampleDao, SampleExtensionDao
{
    fun getOrganizations(userId: String): Flux<Organization> {
        return dslContext.selectOrganizationsByUserId(userId)
    }
    fun getServices(userId: String, categoryId: UUID): Flux<Service> {
        return dslContext.selectServiceByUserIdAndCategoryId(userId, categoryId)
    }
    fun getSampleTypes(serviceId: String): Flux<SampleType> {
        return dslContext.selectSampleTypeByServicId(serviceId)
    }
    fun saveRequest(user: User, requests: Array<Request>): Flux<Request> {
        return Flux.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                Flux.fromArray(requests).flatMap { request ->
                    insertOrder(user.id!!, request.status!!)
                        .flatMap { order ->
                            insertPatient(user.id!!, request.sample!!.patient!!)
                                .then(insertSample(user, request.sample, request.status))
                                .flatMap { sampleRecord ->
                                    val extensions = request.sample.extensions ?: emptyList()
                                    Flux.fromIterable(extensions)
                                        .flatMap { extension ->
                                            insertSampleExtension(extension, sampleRecord.id!!)
                                        }
                                        .then(insertRequest(request.apply {
                                            this.orderId = order.id
                                            this.sample!!.id = sampleRecord.id
                                        }))
                                }
                        }
                }
            }
        })
    }
}