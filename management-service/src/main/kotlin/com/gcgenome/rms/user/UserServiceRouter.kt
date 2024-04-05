package com.gcgenome.rms.user

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.*
import com.gcgenome.rms.exception.*
import org.jooq.exception.DataAccessException
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
class UserServiceRouter (
    private val userServiceHandler: UserServiceHandler,
    private val authenticationHandler: AuthenticationHandler
) {
    @Bean("UserServiceRouter")
    fun route() = router {
        PUT("/w-api/management-service/users/{userId}/services", ::saveUserServices)
    }

    private fun saveUserServices(request: ServerRequest): Mono<ServerResponse> {
        val userId = request.pathVariable("userId")
        return Mono.zip(authenticationHandler.principal(request),request.bodyToMono(Array<Service>::class.java))
            .flatMap { userServiceHandler.insertUserService(it.t1,userId, it.t2).collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it),Service::class.java ) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume (IntegrityConstraintViolationException::class.java) { ServerResponse.status(HttpStatus.NOT_ACCEPTABLE).bodyValue(DatabaseConstraintViolationException().message.toString()) }
            .onErrorResume(UserNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume(ServiceNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume (ServerWebInputException::class.java) { ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(WebInputException().message.toString()) }
            .onErrorResume { e ->  ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: ${e}") }
    }
}

