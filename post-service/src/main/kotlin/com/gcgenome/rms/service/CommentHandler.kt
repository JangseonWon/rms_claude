package com.gcgenome.rms.service

import com.gcgenome.rms.dao.CommentDao
import com.gcgenome.rms.tables.pojos.Comment
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

@Component
class CommentHandler(
    val dslContext: DSLContext
): CommentDao {

    fun insertPostComment(comment: Comment): Mono<Comment> {
        return Mono.from(dslContext.insertComment(comment))
    }

    fun deletePostComment(userId: String, postId: UUID, commentId: UUID): Mono<Comment> {
        return Mono.from(dslContext.deleteComment(userId, postId, commentId))
    }
}