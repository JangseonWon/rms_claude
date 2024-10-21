package com.gcgenome.rms.user

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.UserDTO
import com.gcgenome.rms.data.UserServiceDTO
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
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Configuration
class UserRouter(
    private val userHandler: UserHandler,
    private val authenticationHandler: AuthenticationHandler
) {
    @Bean("UserRouter")
    fun route() = router {
        POST("/w-api/management-service/users", :: findUsers)
        PATCH("/w-api/management-service/users/{userId}", ::updateUser)
        POST("/w-api/management-service/users/{user_id}/services", :: findUserWithServices)
        PUT("/w-api/management-service/users/{user_id}/services/{service_id}", ::saveUserService)
        DELETE("/w-api/management-service/users/{user_id}/services/{service_id}", ::deleteUserServices)
        GET("/w-api/management-service/users/{user_id}/organizations", :: findUserOrganizations)
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

    private fun findUserWithServices(request: ServerRequest): Mono<ServerResponse> {
        val userId = request.pathVariable("user_id")
        return authenticationHandler.chkManager(request)
            .flatMap { request.bodyToMono(Query::class.java).defaultIfEmpty(Query()) }
            .flatMap { query -> userHandler.selectUserWithServices(userId, query) }
            .flatMap { ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(it), UserDTO::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error : $it, ${it.cause}") }
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
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error : $it") }
    }

    private fun updateUser(request: ServerRequest): Mono<ServerResponse> {
        val userId = request.pathVariable("userId")
        return Mono.zip(authenticationHandler.principal(request), request.bodyToMono(UserDTO::class.java))
            .flatMap { p -> userHandler.updateUserById(p.t1, p.t2.apply { id = userId }) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), UserDTO::class.java) }
            .onErrorResume(MatchUserException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}") }
            .onErrorResume (ServerWebInputException::class.java) { ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(WebInputException().message.toString()) }
            .onErrorResume(UserNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error : $it") }
    }

    private fun saveUserService(request: ServerRequest): Mono<ServerResponse> {
        val userId = request.pathVariable("user_id")
        val serviceId = request.pathVariable("service_id")
        return authenticationHandler.chkManager(request)
            .then(userHandler.insertUserService(UserServiceDTO(userId = userId, serviceId = serviceId)))
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), UserDTO::class.java ) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume (IntegrityConstraintViolationException::class.java) { ServerResponse.status(HttpStatus.NOT_ACCEPTABLE).bodyValue(DatabaseConstraintViolationException().message.toString()) }
            .onErrorResume(UserNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume(ServiceNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume (ServerWebInputException::class.java) { ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(WebInputException().message.toString()) }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error : $it") }
    }

    private fun deleteUserServices(request: ServerRequest): Mono<ServerResponse> {
        val userId = request.pathVariable("user_id")
        val serviceId = request.pathVariable("service_id")
        return authenticationHandler.chkManager(request)
            .then(userHandler.deleteUserService(UserServiceDTO(userId = userId, serviceId = serviceId)))
            .flatMap { ServerResponse.status(HttpStatus.OK).bodyValue("${userId}의 서비스 삭제 완료") }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(ManagerAuthenticationException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(ServiceNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error : $it") }
    }
}

