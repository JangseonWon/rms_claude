package com.gcgenome.rms.product

import com.gcgenome.rms.authentication.User
import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.*
import org.jooq.Configuration
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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
    fun updateRequest(user: User, request: Request, orderId: UUID?): Mono<Request> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                insertOrder(user.id!!, null, null)
                    .flatMap { order ->
                        insertPatient(user.id!!, request.sample!!.patient!!)
                            .then(insertSample(user, request.sample, request.status!!))
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