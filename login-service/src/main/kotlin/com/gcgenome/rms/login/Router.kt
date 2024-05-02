package com.gcgenome.rms.login

import com.gcgenome.rms.exceptions.UserNotFoundException
import com.gcgenome.rms.tables.pojos.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseCookie
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration
class Router (
    private val handler: Handler,
    @Value("\${spring.security.oauth2.authorization.jwt.duration}")
    private val duration: Long
) {
    @Bean
    fun route() = router {
        POST("/w-api/login-service/login", ::login)
        POST("/w-api/login-service/signup", ::signup)
    }

    private fun login(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(User::class.java)
            .flatMap { handler.login(it) }
            .map { token -> ResponseCookie.from("Authorization", token).httpOnly(true).path("/w-api")/*.secure(true)*/.maxAge(duration).build() }
            .flatMap { ServerResponse.ok().cookie(it).build() }
            .onErrorResume (UserNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
    }

    private fun signup(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(User::class.java)
            .flatMap { handler.signup(it) }
            .flatMap { ServerResponse.ok().body(Mono.just(it),User::class.java) }
            .onErrorResume (UserNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
    }
}
