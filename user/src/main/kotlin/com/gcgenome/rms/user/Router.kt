package com.gcgenome.rms.user

import com.gcgenome.rms.SecurityContextRepository
import com.gcgenome.rms.data.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration("com.gcgenome.rms.service.Router")
class Router (private val handler: Handler) {
    @Bean("com.gcgenome.rms.service.Router.Bean")
    fun route() = router {
        GET("/api/user", ::findUser)
        POST("/api/user", ::addUser)
        POST("/api/user/organization", ::addOrganization)
        PUT("/api/user/{user-id}/service/{service-id}", ::addUserService)
        DELETE("/api/user/{user-id}", ::deleteUser)
    }
    private fun findUser(request: ServerRequest): Mono<ServerResponse> {
        return request
            .principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .flatMap { handler.selectUser(it.principal) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), User::class.java) }
    }

    private fun addUser(request: ServerRequest): Mono<ServerResponse> {
        return request
            .principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .zipWith(request.bodyToMono(User::class.java))
            .flatMap { handler.insertUser(it.t1.details.id, it.t1.details.authority, it.t2) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), Message::class.java) }
    }

    private fun addOrganization(request: ServerRequest): Mono<ServerResponse> {
        return request
            .principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .zipWith(request.bodyToMono(Organization::class.java))
            .flatMap { handler.insertOrganization(it.t1.principal, it.t1.details.authority, it.t2) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), Organization::class.java) }
            .switchIfEmpty(ServerResponse.status(HttpStatus.NO_CONTENT).build())
            .onErrorResume(DuplicateKeyException::class.java) { ServerResponse.status(HttpStatus.CONFLICT).build() }
            .onErrorResume (ServerResponse.badRequest()::bodyValue)
    }

    private fun addUserService(request: ServerRequest): Mono<ServerResponse> {
        val userId = request.pathVariable("user-id")
        val serviceId = request.pathVariable("service-id")
        return request
            .principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .flatMap {
                ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(handler.insertUserService(it.details.authority, userId, serviceId),
                        UserService::class.java)
            }
            .switchIfEmpty(ServerResponse.status(HttpStatus.NO_CONTENT).build())
            .onErrorResume(DuplicateKeyException::class.java) { ServerResponse.status(HttpStatus.CONFLICT).build() }
            .onErrorResume (ServerResponse.badRequest()::bodyValue)
    }

    private fun deleteUser(request: ServerRequest): Mono<ServerResponse> {
        val userId = request.pathVariable("user-id")
        return request.principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .flatMap { handler.deleteUser(userId, it.details.authority) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), Message::class.java) }
            .switchIfEmpty(ServerResponse.status(HttpStatus.NO_CONTENT).build())
            .onErrorResume(DuplicateKeyException::class.java) { ServerResponse.status(HttpStatus.CONFLICT).build() }
            .onErrorResume (ServerResponse.badRequest()::bodyValue)
    }
}

fun interface HandlerAdapter : (ServerRequest) -> Mono<ServerResponse>

fun ((ServerRequest) -> Mono<ServerResponse>).toHandlerFunction(): HandlerAdapter {
    return HandlerAdapter { request ->
        this.invoke(request)
    }
}