package com.gcgenome.rms.route

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.data.*
import com.gcgenome.rms.exception.*
import com.gcgenome.rms.service.UserHandler
import org.jooq.exception.IntegrityConstraintViolationException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import org.springframework.web.server.ServerWebInputException
import reactor.core.publisher.Mono

@Configuration
class UserRouter (
    private val userHandler: UserHandler
) {
    @Bean("UserRouter")
    fun route() = router {
        PUT("/w-api/management-service/users", ::saveUser)
        PATCH("/w-api/management-service/users/{userId}", ::updateUser)
    }

    private fun saveUser(request: ServerRequest): Mono<ServerResponse> {
        return Mono.zip(principal(request), request.bodyToMono(User::class.java))
            .flatMap { p -> userHandler.insertUser(p.t1, p.t2) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), User::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume (IntegrityConstraintViolationException::class.java) { ServerResponse.status(HttpStatus.NOT_ACCEPTABLE).bodyValue(DatabaseConstraintViolationException().message.toString()) }
            .onErrorResume (ServerWebInputException::class.java) { ServerResponse.status(HttpStatus.NOT_ACCEPTABLE).bodyValue(WebInputException().message.toString()) }
            .onErrorResume { e ->  ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: ${e}") }
    }

    private fun updateUser(request: ServerRequest): Mono<ServerResponse> {
        val userId = request.pathVariable("userId")
        return Mono.zip(principal(request), request.bodyToMono(UpdateUser::class.java))
            .flatMap { p -> userHandler.updateUserById(p.t1, userId, p.t2) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), User::class.java) }
            .onErrorResume(MatchUserException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}") }
            .onErrorResume (ServerWebInputException::class.java) { ServerResponse.status(HttpStatus.NOT_ACCEPTABLE).bodyValue(WebInputException().message.toString()) }
            .onErrorResume(UserNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume { e ->  ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: ${e}") }
    }

    private fun principal(request: ServerRequest): Mono<UserAuthentication> {
        return request.principal().switchIfEmpty(Mono.error(AuthenticationNotFoundException()))
            .cast(UserAuthentication::class.java)
    }
}

