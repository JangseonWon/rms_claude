package com.gcgenome.rms.route

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.exceptions.AuthenticationNotFoundException
import com.gcgenome.rms.service.CommentHandler
import com.gcgenome.rms.tables.pojos.Comment
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import java.util.*

@Configuration
class CommentRouter (
    private val authenticationHandler: AuthenticationHandler,
    private val serviceHandler: CommentHandler
) {
    @Bean("CommentRouter")
    fun route() = router {
        POST("/w-api/post-service/post/comment", ::insertPostComment)
        DELETE("/w-api/post-service/post/{post_id}/comment/{comment_id}", ::deletePostComment)
    }

    private fun insertPostComment(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.principal(request)
            .flatMap { request.bodyToMono(Comment::class.java) }
            .flatMap { serviceHandler.insertPostComment(it) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Request body error.") }
    }

    private fun deletePostComment(request: ServerRequest): Mono<ServerResponse> {
        val postId = UUID.fromString(request.pathVariable("post_id"))
        val commentId = UUID.fromString(request.pathVariable("comment_id"))
        return authenticationHandler.principal(request)
            .flatMap { serviceHandler.deletePostComment(it.user.id!!, postId, commentId) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Request body error.") }
    }
}

