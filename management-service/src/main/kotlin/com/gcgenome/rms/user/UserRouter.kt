package com.gcgenome.rms.user

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.UserDTO
import com.gcgenome.rms.exception.*
import com.gcgenome.rms.tables.pojos.User
import org.jooq.exception.DataAccessException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import org.springframework.web.server.ServerWebInputException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Configuration
class UserRouter(
    private val userHandler: UserHandler,
    private val authenticationHandler: AuthenticationHandler
) {
    @Bean("UserRouter")
    fun route() = router {
        GET("/w-api/management-service/users/{user-id}", :: findUser)
        POST("/w-api/management-service/users/search", :: findUsers)
        PUT("/w-api/management-service/user", :: insertManager)
        PATCH("/w-api/management-service/users/{user-id}", ::updateUser)
        GET("/w-api/management-service/users/{user-id}/organizations", :: findUserOrganizations)
    }
    private fun findUser(request: ServerRequest): Mono<ServerResponse> {
        val userId = request.pathVariable("user-id")
        return authenticationHandler.chkManager(request)
            .flatMap { userHandler.selectUser(userId) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), UserDTO::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error : $it") }
    }
    private fun findUsers(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.chkManager(request)
            .flatMap { request.bodyToMono(Query::class.java).defaultIfEmpty(Query()) }
            .flatMap { query -> userHandler.selectUsers(query) }
            .flatMap { ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Total-Count", it.totalCount.toString())
                .header("X-Total-Page", it.totalPage.toString())
                .header("X-Page-Size", it.pageSize.toString())
                .header("X-Current-Page", it.currentPage.toString())
                .body(Flux.fromIterable(it.data), UserDTO::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error : $it") }
    }

    private fun insertManager(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.chkManager(request).zipWith(request.bodyToMono(User::class.java))
            .flatMap { userHandler.insertManager(it.t1.user.id!!, it.t2) }
            .flatMap { ServerResponse.ok().build() }
            .onErrorResume (ServerWebInputException::class.java) { ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(WebInputException().message.toString()) }
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}")}
            .onErrorResume (DataAccessException::class.java) { ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("A duplicate ID exists.") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error : $it") }
    }

    private fun findUserOrganizations(request: ServerRequest): Mono<ServerResponse> {
        val userId = request.pathVariable("user-id")
        return authenticationHandler.principal(request)
            .then(userHandler.selectUserOrganizations(userId).collectList())
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(ServerWebInputException::class.java) { ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(WebInputException().message.toString()) }
            .onErrorResume(UserNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume(DataAccessException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(ColumnNotFoundException(e).message) }
            .onErrorResume(IllegalArgumentException::class.java) { ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(FilterOperatorNotFoundException().message.toString())}
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error : $it") }
    }

    private fun updateUser(request: ServerRequest): Mono<ServerResponse> {
        val userId = request.pathVariable("user-id")
        return authenticationHandler.chkManager(request).zipWith(request.bodyToMono(UserDTO::class.java))
            .flatMap { userHandler.updateUserById(it.t1, it.t2.apply { id = userId }) }
            .flatMap { ServerResponse.ok().build() }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(MatchUserException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}") }
            .onErrorResume (ServerWebInputException::class.java) { ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(WebInputException().message.toString()) }
            .onErrorResume(UserNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error : $it") }
    }
}

