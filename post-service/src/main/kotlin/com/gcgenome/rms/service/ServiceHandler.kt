package com.gcgenome.rms.service

import com.gcgenome.rms.auth.ManagerAuthenticationHandler
import com.gcgenome.rms.dao.PostDao
import com.gcgenome.rms.data.Post_
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.exceptions.FilterOperatorNotFoundException
import com.gcgenome.rms.tables.references.POST
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jooq.impl.DSL.field
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Component
class ServiceHandler(
    val dslContext: DSLContext,
    private val managerAuthenticationHandler: ManagerAuthenticationHandler
): PostDao {

    fun getPostAll(query: Query): Flux<Post_> {
        val whereClause = buildWhereClause(query.filters)
        return Flux.from(dslContext.selectPostAll(query, whereClause))
    }

    fun getPostByPostId(postId: UUID): Mono<Post_> {
        return Mono.from(dslContext.selectPostById(postId))
    }

    fun buildWhereClause(filters:List<Query.Companion.Filter>?) : Condition {
        return filters?.let {
            it.filter { filter -> filter.key != null && filter.value?.isNotBlank() == true }
                .map { filter ->
                    val key = filter.key!!
                    val value = filter.value!!
                    val field = when (key) {
                        "title" -> POST.TITLE
                        "user_id" -> POST.USER_ID
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