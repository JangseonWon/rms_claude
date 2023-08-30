package com.gcgenome.rms.service

import com.gcgenome.rms.dao.UserServiceDao
import com.gcgenome.rms.data.Page
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.Service
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class UserServiceHandler(
    val dslContext: DSLContext
): UserServiceDao {

    fun selectUserServiceById(query: Query, userId: String): Mono<Page<Service>> {
        val userServices = dslContext.selectUserServiceById(query, userId)
        return dslContext.selectUserServiceCount(query, userId)
            .flatMap { totalCount ->
                val totalPage = (totalCount + query.size - 1) / query.size
                val page = Page(totalCount, totalPage, query.size, query.page, userServices)
                Mono.just(page)
            }
    }
}