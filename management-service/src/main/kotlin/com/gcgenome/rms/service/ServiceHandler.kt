package com.gcgenome.rms.service

import com.gcgenome.rms.auth.ManagerAuthenticationHandler
import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.dao.SampleTypeDao
import com.gcgenome.rms.dao.ServiceDao
import com.gcgenome.rms.dao.ServiceExtensionDao
import com.gcgenome.rms.dao.UserServiceDao
import com.gcgenome.rms.data.Page
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.ServiceCategory
import com.gcgenome.rms.data.Service_
import com.gcgenome.rms.exception.FilterOperatorNotFoundException
import com.gcgenome.rms.exception.ServiceNotFoundException
import com.gcgenome.rms.tables.pojos.Service
import com.gcgenome.rms.tables.references.CATEGORY
import com.gcgenome.rms.tables.references.SERVICE
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jooq.impl.DSL.field
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class ServiceHandler(
    val dslContext: DSLContext,
    private val managerAuthenticationHandler: ManagerAuthenticationHandler
): ServiceDao, ServiceExtensionDao, SampleTypeDao, UserServiceDao {

    fun checkServiceById(serviceId: String): Mono<Service_> {
        return dslContext.selectServiceById(serviceId)
            .switchIfEmpty(Mono.error(ServiceNotFoundException(serviceId)))
    }

    fun updateServiceById(service: Service): Mono<Service> {
        return checkServiceById(service.id!!).flatMap {
            dslContext.updateServiceById(service)
        }
    }

    fun selectServiceByNameOrId(filter: Query.Companion.Filter): Flux<Service> {
        val whereClause = buildServiceIdOrNameWhereClause(filter)
        return Flux.from(dslContext.selectServiceByNameOrId(whereClause))
    }

    fun selectServiceCategory(authentication: UserAuthentication, query: Query): Mono<Page<ServiceCategory>> {
        val whereClause = buildWhereClause(query.filters)
        return managerAuthenticationHandler.chkManager(authentication)
            .flatMap {
                val services = dslContext.dsl().selectServiceAndCategory(query, whereClause)
                dslContext.selectServicesAndCategoryCount(whereClause)
                    .flatMap { totalCount ->
                        val totalPage = (totalCount + query.size -1) / query.size
                        services.collectList().flatMap { serviceList ->
                            dslContext.getUserService().collectList().flatMap { userServices ->
                                val serviceCategoryList = serviceList.map { serviceCategory ->
                                    val state = userServices.any { it.serviceId == serviceCategory.serviceId }
                                    serviceCategory.copy(state = state)
                                }
                                val page = Page(totalCount, totalPage, query.size, query.page + 1, serviceCategoryList)
                                Mono.just(page)
                            }
                        }
                    }
            }
    }

    fun selectServiceByUserId(userId: String, filter: Query.Companion.Filter): Flux<Service_> {
        val whereClause = buildServiceIdOrNameWhereClause(filter)
        return Flux.from(dslContext.selectServiceByUserId(userId, whereClause))
    }

    fun selectService(serviceId: String): Mono<Service_> {
        return Mono.from(dslContext.selectServiceByServiceId(serviceId))
    }

    fun buildServiceIdOrNameWhereClause(filter: Query.Companion.Filter) : Condition {
        return field("service.id").likeIgnoreCase("%${filter.value}%")
            .or(field("service.name").likeIgnoreCase("%${filter.value}%"))
    }

    fun buildWhereClause(filters:List<Query.Companion.Filter>?) : Condition {
        return filters?.let {
            it.filter { filter -> filter.key != null && filter.value?.isNotBlank() == true }
                .map { filter ->
                    val key = filter.key!!
                    val value = filter.value!!
                    val field = when (key) {
                        "service_id" -> SERVICE.ID
                        "service_name" -> SERVICE.NAME
                        "category_id" -> CATEGORY.ID
                        "category_name" -> CATEGORY.NAME
                        else -> throw IllegalArgumentException("Unknown filter key: $key")
                    }
                    val condition = when (filter.operator) {
                        "=" -> field(key).eq(value)
                        "LIKE" -> field.likeIgnoreCase("%$value%")
                        ">" -> field(key).gt(value)
                        "<" -> field(key).lt(value)
                        ">=" -> field(key).ge(value)
                        "<=" -> field(key).le(value)
                        else -> throw FilterOperatorNotFoundException()
                    }
                    condition
                }
                .reduceOrNull { acc, condition -> acc.and(condition) } ?: DSL.trueCondition()
        } ?: DSL.trueCondition()
    }
}