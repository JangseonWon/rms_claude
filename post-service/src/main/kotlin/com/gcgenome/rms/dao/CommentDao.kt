package com.gcgenome.rms.dao

import com.gcgenome.rms.data.CommentDTO
import com.gcgenome.rms.tables.references.COMMENT
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

interface CommentDao {
    fun DSLContext.insertComment(comment: CommentDTO): Mono<CommentDTO> {
        return Mono.from(
            insertInto(COMMENT)
                .set(COMMENT.ID, UUID.randomUUID())
                .set(COMMENT.CONTENT, comment.content)
                .set(COMMENT.CREATE_AT, comment.createAt ?: LocalDateTime.now())
                .set(COMMENT.LAST_MODIFY_AT, LocalDateTime.now())
                .set(COMMENT.POST_ID, comment.post!!.id)
                .set(COMMENT.USER_ID, comment.user!!.id)
                .returning()
        ).map { it.into(CommentDTO::class.java) }
    }

    fun DSLContext.deleteComment(commentId: UUID): Mono<CommentDTO> {
        return Mono.from(
            deleteFrom(COMMENT)
                .where(COMMENT.ID.eq(commentId))
                .returning()
        ).map { it.into(CommentDTO::class.java) }
    }

    fun DSLContext.deleteCommentByPostId(postId: UUID): Mono<CommentDTO> {
        return Mono.from(
            deleteFrom(COMMENT)
                .where(COMMENT.POST_ID.eq(postId))
                .returning()
        ).map { it.into(CommentDTO::class.java) }
    }
}