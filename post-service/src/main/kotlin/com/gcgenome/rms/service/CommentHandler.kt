package com.gcgenome.rms.service

import com.gcgenome.rms.dao.CommentDao
import com.gcgenome.rms.data.CommentDTO
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

@Component
class CommentHandler(
    val dslContext: DSLContext
): CommentDao {
    fun insertComment(comment: CommentDTO): Mono<CommentDTO> {
        return Mono.from(dslContext.insertComment(comment))
    }

    fun deleteComment(commentId: UUID): Mono<CommentDTO> {
        return Mono.from(dslContext.deleteComment(commentId))
    }
}