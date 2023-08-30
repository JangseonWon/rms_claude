package com.gcgenome.rms.service

import com.gcgenome.rms.dao.ServiceDao
import com.gcgenome.rms.dao.UserDao
import com.gcgenome.rms.dao.UserServiceDao
import com.gcgenome.rms.data.Page
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.Service
import com.gcgenome.rms.data.UserService
import com.gcgenome.rms.exception.ServiceNotFoundException
import com.gcgenome.rms.exception.UserNotFoundException
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class UserServiceHandler(
    val dslContext: DSLContext
): UserServiceDao, ServiceDao, UserDao {

    fun selectUserServiceById(query: Query, userId: String): Mono<Page<Service>> {
        val userServices = dslContext.selectUserServicesById(query, userId)
        return dslContext.selectUserServiceCount(query, userId)
            .flatMap { totalCount ->
                val totalPage = (totalCount + query.size - 1) / query.size
                val page = Page(totalCount, totalPage, query.size, query.page, userServices)
                Mono.just(page)
            }
    }
    fun insertUserService(userId: String, itemId: String): Mono<UserService> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                selectServiceById(itemId).switchIfEmpty(Mono.error(ServiceNotFoundException(itemId)))
                    .flatMap { selectUserById(userId).switchIfEmpty(Mono.error(UserNotFoundException(userId))) }
                    .flatMap { insertUserService(userId, itemId) }
                    .flatMap { selectUserServiceById(userId, itemId) }
            }
        })
    }
}