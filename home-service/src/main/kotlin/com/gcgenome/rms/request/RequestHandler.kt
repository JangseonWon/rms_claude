package com.gcgenome.rms.request

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.dao.RequestDao
import com.gcgenome.rms.data.Page
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.RequestDTO
import com.gcgenome.rms.data.Role
import org.jooq.DSLContext
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RequestHandler(
    val dslContext: DSLContext
): RequestDao {
    fun selectRequests(authentication: UserAuthentication, query: Query):  Mono<Page<RequestDTO>> {
        authentication.takeIf { it.user.role == Role.USER.toString() }?.let {
            query.filterGroups = query.filterGroups ?: mutableListOf()  // null 체크 및 초기화
            query.filterGroups?.add(
                Query.FilterGroup(
                    filters = listOf(
                        Query.FilterGroup.Filter(
                            table = "order",
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