package com.gcgenome.rms.service

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.dao.CommentDao
import com.gcgenome.rms.dao.PostDao
import com.gcgenome.rms.data.Page
import com.gcgenome.rms.data.Post_
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.Role
import com.gcgenome.rms.exceptions.FilterOperatorNotFoundException
import com.gcgenome.rms.tables.pojos.Comment
import com.gcgenome.rms.tables.references.POST
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jooq.impl.DSL.field
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

@Component
class ServiceHandler(
    val dslContext: DSLContext
): PostDao, CommentDao {
    fun pageCount(query: Query, totalCount: Int) : Int{
        var totalPage = totalCount / query.size
        if (totalCount % query.size != 0) totalPage++
        return totalPage
    }

    fun getPostAll(authentication: UserAuthentication, query: Query):  Mono<Page<Post_>> {
        var whereClause = buildWhereClause(query.filters)
        if (authentication.user.role == Role.USER.toString()) {
            whereClause = whereClause.and(POST.USER_ID.eq(authentication.user.id))
        }
        val postData = dslContext.selectPostAll(query, whereClause)
        return dslContext.selectPostCount(query, whereClause)
            .flatMap { totalCount ->
                postData.collectList().flatMap { list ->
                    val page = Page(totalCount, pageCount(query, totalCount), query.size, query.page + 1, list)
                    Mono.just(page)
                }
            }
    }

    fun getPostByPostId(postId: UUID): Mono<Post_> {
        return Mono.from(dslContext.selectPostById(postId))
    }

    fun insertPostComment(comment: Comment): Mono<Comment> {
        return Mono.from(dslContext.insertComment(comment))
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