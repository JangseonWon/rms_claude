package com.gcgenome.rms.dashboard

import com.gcgenome.rms.authentication.User
import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.dao.RequestDao
import com.gcgenome.rms.data.Page
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.RequestDTO
import com.gcgenome.rms.data.StatusCount
import org.jooq.DSLContext
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class Handler(
    val dslContext: DSLContext
): RequestDao {
    fun selectStatus(user : User) : Mono<StatusCount> {
        return dslContext.selectStatusCount(user)
    }

    fun selectRequests(user: UserAuthentication, query: Query, status: String):  Mono<Page<RequestDTO>> {
        return dslContext.selectRequestsWithPage(query, user.user)
    }
}