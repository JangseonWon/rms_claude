package com.gcgenome.rms.service

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.dao.CommentDao
import com.gcgenome.rms.dao.PostDao
import com.gcgenome.rms.dao.PostFileDao
import com.gcgenome.rms.data.*
import com.gcgenome.rms.exceptions.FilterOperatorNotFoundException
import com.gcgenome.rms.tables.pojos.Post
import com.gcgenome.rms.tables.references.POST
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jooq.impl.DSL.field
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.util.*

@Component
class PostHandler(
    val dslContext: DSLContext
): PostDao, PostFileDao, CommentDao {
    private val webClient: WebClient = WebClient.builder()
        .baseUrl("https://wh.jandi.com/connect-api/webhook")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.tosslab.jandi-v2+json")
        .build()

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

    fun insertPost(authentication: UserAuthentication, post: Post): Mono<Post> {
        val userId = authentication.user.id
        return Mono.from(dslContext.insertPost(userId, post))
    }
    fun deletePost(postId: UUID): Mono<Post> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                deletePostFile(postId)
                    .then(deleteCommentByPostId(postId))
                    .then(deletePostByPostId(postId))
            }
        })
    }

    fun postIdCheckSwitch(postId: UUID, new: Boolean): Mono<Post> {
        return Mono.from(dslContext.readChangeByPostId(postId, new))
    }

    fun sendToJandi(postId: UUID, category: String, jandiRequest: JandiRequest): Mono<Post> {
        val (connectColor, postCategory, comment) = when (category.lowercase()) {
            "bug" -> Triple("#cd4855", "새로운 질문이 등록 되었습니다.", "")
            "result" -> Triple("#007bff", "새로운 질문이 등록 되었습니다.", "")
            "service" -> Triple("#28a745", "새로운 질문이 등록 되었습니다.", "")
            "others" -> Triple("#a75928", "새로운 질문이 등록 되었습니다.", "")
            "comment" -> Triple("#FFFA99", "새로운 댓글이 등록 되었습니다.", "\n댓글: ${jandiRequest.comment?.content}")
            else -> Triple("#000000", "새로운 질문이 등록 되었습니다.", "")
        }

        val requestBody = mapOf(
            "body" to "[$postCategory](https://rms-test.gcgenome.com/qna/${jandiRequest.post.userId}/${postId})",
            "connectColor" to connectColor,
            "connectInfo" to listOf(
                mapOf("title" to "세부내용", "description" to "제목: ${jandiRequest.post.title}\n작성자: ${jandiRequest.userName}$comment")
            )
        )

        return webClient.post()
            .uri("/17558388/81bc697c503328e485da5e22dddaaa4e")
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(Post::class.java)
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