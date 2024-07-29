package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.pojos.Comment
import com.gcgenome.rms.tables.references.COMMENT
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

interface CommentDao {
    fun DSLContext.insertComment(comment: Comment): Mono<Comment> {
        return Mono.from(
            insertInto(COMMENT)
                .set(COMMENT.ID, UUID.randomUUID())
                .set(COMMENT.CONTENT, comment.content)
                .set(COMMENT.CREATE_AT, comment.createAt ?: LocalDateTime.now())
                .set(COMMENT.LAST_MODIFY_AT, LocalDateTime.now())
                .set(COMMENT.POST_ID, comment.postId)
                .set(COMMENT.USER_ID, comment.userId)
                .returning()
        ).map { it.into(Comment::class.java) }
    }
}