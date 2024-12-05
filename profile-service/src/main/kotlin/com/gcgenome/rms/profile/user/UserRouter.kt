package com.gcgenome.rms.profile.user

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.UserDTO
import com.gcgenome.rms.exceptions.AuthenticationNotFoundException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration
class UserRouter (
    private val authenticationHandler: AuthenticationHandler,
    private val userHandler: UserHandler
) {
    @Bean("UserRouter")
    fun route() = router {
        GET("/w-api/profile-service/users/{user-id}", ::findUser)
        PATCH("/w-api/profile-service/users/{user-id}", ::updateUser)
    }

    private fun findUser(request: ServerRequest): Mono<ServerResponse> {
        val userId = request.pathVariable("user-id")
        return authenticationHandler.chkUser(request, userId)
            .then(userHandler.selectUserById(userId))
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), UserDTO::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error: ${it.cause}") }
    }

    private fun updateUser(request: ServerRequest): Mono<ServerResponse> {
        val userId = request.pathVariable("user-id")
        return authenticationHandler.chkUser(request, userId).zipWith(request.bodyToMono(UserDTO::class.java))
            .flatMap { userHandler.updateUserById(it.t1, it.t2.apply { id = userId }) }
            .flatMap { ServerResponse.ok().build() }
            .onErrorResume (AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume (IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error: ${it.cause}") }
    }
}

