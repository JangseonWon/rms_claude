package com.gcgenome.rms.user

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.auth.ManagerAuthenticationHandler
import com.gcgenome.rms.data.*
import com.gcgenome.rms.exception.*
import com.gcgenome.rms.tables.pojos.Organization
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
import java.util.*

@Configuration
class UserRouter(
    private val userHandler: UserHandler,
    private val authenticationHandler: AuthenticationHandler
) {
    @Bean("UserRouter")
    fun route() = router {
        GET("/w-api/management-service/users/{user_id}/organizations", :: findUserOrganizations)
        POST("/w-api/management-service/users", :: findUsers)
        PUT("/w-api/management-service/users", ::saveUser)
        PUT("/w-api/management-service/users/{user_id}/organizations", ::saveUserOrganization)
        PATCH("/w-api/management-service/users/{userId}", ::updateUser)
    }

    private fun findUsers(request: ServerRequest): Mono<ServerResponse> {
        return Mono.zip(authenticationHandler.principal(request),request.bodyToMono(Query::class.java))
            .flatMap { p -> userHandler.selectUsers(p.t1,p.t2.copy(page=p.t2.page - 1)) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(ServerWebInputException::class.java) { ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(WebInputException().message.toString()) }
            .onErrorResume(DataAccessException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(ColumnNotFoundException(e).message) }
            .onErrorResume(IllegalArgumentException::class.java) { ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(FilterOperatorNotFoundException().message.toString())}
            .onErrorResume { e ->  ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: ${e}") }
    }

    private fun findUserOrganizations(request: ServerRequest): Mono<ServerResponse> {
        val userId = request.pathVariable("user_id")
        return authenticationHandler.principal(request)
            .then(userHandler.selectUserOrganizations(userId).collectList())
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(ServerWebInputException::class.java) { ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(WebInputException().message.toString()) }
            .onErrorResume(UserNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume(DataAccessException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(ColumnNotFoundException(e).message) }
            .onErrorResume(IllegalArgumentException::class.java) { ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(FilterOperatorNotFoundException().message.toString())}
            .onErrorResume { e ->  ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $e") }
    }

    private fun saveUser(request: ServerRequest): Mono<ServerResponse> {
        return Mono.zip(authenticationHandler.principal(request), request.bodyToMono(User::class.java))
            .flatMap { p -> userHandler.insertUser(p.t1, p.t2) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), User::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume (IntegrityConstraintViolationException::class.java) { ServerResponse.status(HttpStatus.NOT_ACCEPTABLE).bodyValue(DatabaseConstraintViolationException().message.toString()) }
            .onErrorResume (ServerWebInputException::class.java) { ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(WebInputException().message.toString()) }
            .onErrorResume { e ->  ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: ${e}") }
    }

    private fun saveUserOrganization(request: ServerRequest): Mono<ServerResponse> {
        val userId = request.pathVariable("user_id")
        return Mono.zip(authenticationHandler.principal(request), request.bodyToMono(Organization::class.java))
            .flatMap { userHandler.insertUserOrganization(userId, it.t1, it.t2) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), Organization_::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(UserNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume (IntegrityConstraintViolationException::class.java) { ServerResponse.status(HttpStatus.NOT_ACCEPTABLE).bodyValue(DatabaseConstraintViolationException().message.toString()) }
            .onErrorResume (ServerWebInputException::class.java) { ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(WebInputException().message.toString()) }
            .onErrorResume { e ->  ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: ${e}") }
    }

    private fun updateUser(request: ServerRequest): Mono<ServerResponse> {
        val userId = request.pathVariable("userId")
        return Mono.zip(authenticationHandler.principal(request), request.bodyToMono(UpdateUser::class.java))
            .flatMap { p -> userHandler.updateUserById(p.t1, userId, p.t2) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), User::class.java) }
            .onErrorResume(MatchUserException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}") }
            .onErrorResume (ServerWebInputException::class.java) { ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(WebInputException().message.toString()) }
            .onErrorResume(UserNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume { e ->  ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: ${e}") }
    }

}

