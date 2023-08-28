package com.gcgenome.rms.user

import com.gcgenome.rms.config.SecurityContextRepository.UserAuthentication
import com.gcgenome.rms.data.*
import com.gcgenome.rms.exception.ManagerAuthenticationException
import com.gcgenome.rms.exception.MatchUserException
import com.gcgenome.rms.exception.UserNotFoundException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration
class Router (private val userHandler: Handler) {
    @Bean
    fun route() = router {
        GET("/w-api/users", ::findUsers)
        GET("/w-api/users/{userId}", ::findUser)
        PATCH("/w-api/users/{userId}", ::updateUser)
    }
    /*fun userRoute(): RouterFunction<ServerResponse> {
        return route().GET("/w-api/users", this::findUsers) { ops -> ops.beanClass(Handler::class.java).beanMethod("selectUsers") }.build()
            .and(route().GET("/w-api/users/{userId}", this::findUser) {ops -> ops.beanClass(Handler::class.java).beanMethod("selectUserById")}.build())
            .and(route().PATCH("/w-api/users/{userId}", this::updateUser) {ops -> ops.beanClass(Handler::class.java).beanMethod("updateUserById")}.build())
    }*/
    private fun findUsers(request: ServerRequest): Mono<ServerResponse> {
        return principal(request)
            .flatMap { userHandler.chkManager(it) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(userHandler.selectUsers(), User::class.java) }
            .onErrorResume(ManagerAuthenticationException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}") }
    }
    private fun findUser(request: ServerRequest): Mono<ServerResponse> {
        val userId = request.pathVariable("userId")
        return principal(request, userId)
            .flatMap { userHandler.selectUserById(userId) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), User::class.java) }
            .onErrorResume(MatchUserException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}") }
            .onErrorResume(UserNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
    }
    private fun updateUser(request: ServerRequest): Mono<ServerResponse> {
        val userId = request.pathVariable("userId")
        return principal(request, userId)
            .flatMap { request.bodyToMono(User::class.java) }
            .flatMap { userHandler.updateUserById(userId, it) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), User::class.java) }
            .onErrorResume(MatchUserException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}") }
            .onErrorResume(UserNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
    }

    private fun principal(request: ServerRequest, userId: String): Mono<UserAuthentication> {
        return request.principal().cast(UserAuthentication::class.java)
            .flatMap { userHandler.matchUser(it, userId) }
    }
    private fun principal(request: ServerRequest): Mono<UserAuthentication> {
        return request.principal().cast(UserAuthentication::class.java)
    }
}

