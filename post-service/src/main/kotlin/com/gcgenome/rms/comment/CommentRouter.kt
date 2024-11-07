package com.gcgenome.rms.comment

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.CommentDTO
import com.gcgenome.rms.data.PostDTO
import com.gcgenome.rms.data.UserDTO
import com.gcgenome.rms.exception.AuthenticationNotFoundException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
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
        PUT("/w-api/post-service/post/{post-id}/comment", ::insertPostComment)
        DELETE("/w-api/post-service/comment/{comment-id}", ::deletePostComment)
    }

    private fun insertPostComment(request: ServerRequest): Mono<ServerResponse> {
        val postId = UUID.fromString(request.pathVariable("post-id"))
        return Mono.zip(authenticationHandler.principal(request), request.bodyToMono(CommentDTO::class.java))
            .flatMap { serviceHandler.insertComment(it.t2.apply {
                post = PostDTO(id = postId)
                user = UserDTO(id = it.t1.user.id)
            }) }
            .flatMap { ServerResponse.ok().build() }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error: ${it.cause}") }
    }

    private fun deletePostComment(request: ServerRequest): Mono<ServerResponse> {
        val commentId = UUID.fromString(request.pathVariable("comment-id"))
        return authenticationHandler.principal(request)
            .flatMap { serviceHandler.deleteComment(commentId) }
            .flatMap { ServerResponse.ok().build() }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Request body error.") }
    }
}

