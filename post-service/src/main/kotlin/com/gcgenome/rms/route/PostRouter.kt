package com.gcgenome.rms.route

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.exceptions.AuthenticationNotFoundException
import com.gcgenome.rms.service.PostHandler
import com.gcgenome.rms.tables.pojos.Post
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
class PostRouter (
    private val authenticationHandler: AuthenticationHandler,
    private val serviceHandler: PostHandler
) {
    @Bean("PostRouter")
    fun route() = router {
        POST("/w-api/post-service/post/search", ::selectPostSearch)
        GET("/w-api/post-service/post/{post_id}", ::selectPostByPostId)
        PATCH("/w-api/post-service/post/{post_id}", ::postIdCheckSwitch)
        POST("/w-api/post-service/post", ::insertPost)
        DELETE("/w-api/post-service/post/{post_id}", ::deletePost)
    }

    private fun selectPostSearch(request: ServerRequest): Mono<ServerResponse> {
        return Mono.zip(authenticationHandler.principal(request), request.bodyToMono(Query::class.java))
            .flatMap { serviceHandler.getPostAll(it.t1, it.t2.copy(page= it.t2.page -1 )) }
            .flatMap { ServerResponse.ok()
                .header("X-Total-Page", it.totalPage.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Request body error.") }
    }

    private fun selectPostByPostId(request: ServerRequest): Mono<ServerResponse> {
        val postId = UUID.fromString(request.pathVariable("post_id"))
        return authenticationHandler.principal(request)
            .flatMap { serviceHandler.getPostByPostId(postId) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Request body error.") }
    }

    private fun insertPost(request: ServerRequest): Mono<ServerResponse> {
        return Mono.zip(authenticationHandler.principal(request), request.bodyToMono(Post::class.java))
            .flatMap { serviceHandler.insertPost(it.t1, it.t2) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Request body error.") }
    }

    private fun deletePost(request: ServerRequest): Mono<ServerResponse> {
        val postId = UUID.fromString(request.pathVariable("post_id"))
        return authenticationHandler.principal(request)
            .flatMap { serviceHandler.deletePost(postId) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Request body error.") }
    }

    private fun postIdCheckSwitch(request: ServerRequest): Mono<ServerResponse> {
        val postId = UUID.fromString(request.pathVariable("post_id"))
        return authenticationHandler.principal(request)
            .flatMap { serviceHandler.postIdCheckSwitch(postId) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Request body error.") }
    }
}

