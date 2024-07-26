package com.gcgenome.rms.route

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.auth.ManagerAuthenticationHandler
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.exceptions.AuthenticationNotFoundException
import com.gcgenome.rms.service.ServiceHandler
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
class Router (
    private val authenticationHandler: AuthenticationHandler,
    private val managerAuthenticationHandler: ManagerAuthenticationHandler,
    private val serviceHandler: ServiceHandler
) {
    @Bean
    fun route() = router {
        POST("/w-api/post_service/post/search", ::selectPostSearch)
        GET("/w-api/post_service/post/{post_id}", ::selectPostByPostId)
    }

    private fun selectPostSearch(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.principal(request)
            .flatMap { request.bodyToMono(Query::class.java) }
            .flatMap { serviceHandler.getPostAll(it).collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
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
}

