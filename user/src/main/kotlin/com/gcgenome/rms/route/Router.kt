package com.gcgenome.rms.route

import com.gcgenome.rms.config.SecurityContextRepository.UserAuthentication
import com.gcgenome.rms.data.*
import com.gcgenome.rms.exception.ManagerAuthenticationException
import com.gcgenome.rms.exception.MatchUserException
import com.gcgenome.rms.exception.UserNotFoundException
import com.gcgenome.rms.service.UserHandler
import com.gcgenome.rms.service.UserServiceHandler
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
class Router (
    private val userHandler: UserHandler,
    private val userServiceHandler: UserServiceHandler
) {
    @Bean
    fun route() = router {
        POST("/w-api/users", ::findUsers)
        GET("/w-api/users/{userId}", ::findUser)
        PATCH("/w-api/users/{userId}", ::updateUser)
        POST("/w-api/users/{userId}", :: saveUser)
        POST("/w-api/users/{userId}/items", :: findUserService)
    }
    private fun findUsers(request: ServerRequest): Mono<ServerResponse> {
        return principal(request)
            .flatMap { userHandler.chkManager(it) }
            .flatMap { request.bodyToMono(Query::class.java) }
            .flatMap { userHandler.selectUsers(it.copy(page = it.page-1)) }
            .flatMap {
                ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .header("X-Total_Count", it.totalCount.toString())
                .header("X-Total_Count", it.totalPage.toString())
                .header("X-Current-Page", it.currentPage.toString())
                .body(it.data, User::class.java)
            }.onErrorResume { throwable->
                when(throwable) {
                    is ManagerAuthenticationException -> { ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${throwable.message}") }
                    else -> { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $throwable")}
                }
            }
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
    private fun saveUser(request: ServerRequest): Mono<ServerResponse> {
        val userId = request.pathVariable("userId")
        return principal(request)
            .flatMap { userHandler.chkManager(it) }
            .flatMap { request.bodyToMono(User::class.java) }
            .flatMap { userHandler.insertUser(it) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), User::class.java) }
            .onErrorResume { throwable->
                when(throwable) {
                    is IntegrityConstraintViolationException -> { ServerResponse.status(HttpStatus.NOT_ACCEPTABLE).bodyValue("중복된 ID가 있습니다.") }
                    is ServerWebInputException -> { ServerResponse.status(HttpStatus.NOT_ACCEPTABLE).bodyValue("누락된 정보 또는 잘못입력된 정보가 있습니다.")}
                    else -> { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: ${throwable}")}
                }
            }
    }
    private fun findUserService(request: ServerRequest): Mono<ServerResponse> {
        val userId = request.pathVariable("userId")
        return principal(request, userId)
            .flatMap { request.bodyToMono(Query::class.java) }
            .flatMap { userServiceHandler.selectUserServiceById(it.copy(page = it.page-1), userId) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .header("X-Total_Count", it.totalCount.toString())
                .header("X-Total_Count", it.totalPage.toString())
                .header("X-Current-Page", it.currentPage.toString())
                .body(it.data, Service::class.java )
            }
    }

    private fun principal(request: ServerRequest, userId: String): Mono<UserAuthentication> {
        return request.principal().cast(UserAuthentication::class.java)
            .flatMap { userHandler.matchUser(it, userId) }
    }
    private fun principal(request: ServerRequest): Mono<UserAuthentication> {
        return request.principal().cast(UserAuthentication::class.java)
    }

}

