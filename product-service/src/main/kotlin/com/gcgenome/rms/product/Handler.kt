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
import reactor.core.publisher.Mono
import java.util.*

@Component
class Handler(val dslContext: DSLContext):
    OrganizationDao, ServiceDao, SampleTypeDao, OrderDao, RequestDao, PatientDao, SampleDao
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
    fun saveRequest(user: User, request: Request): Mono<Request> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                insertOrder(user.id!!, request.status!!)
                    .flatMap { order ->
                        insertPatient(user.id!!, request.sample!!.patient!!)
                            .then(insertSample(user, request.sample, request.status))
                            .flatMap {
                                insertRequest(request.apply {
                                    this.orderId = order.id
                                    this.sample!!.id = it.id
                                })
                            }
                    }
            }
        })
    }
}