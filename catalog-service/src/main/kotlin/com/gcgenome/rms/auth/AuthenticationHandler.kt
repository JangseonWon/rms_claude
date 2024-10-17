package com.gcgenome.rms.auth

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.data.Role
import com.gcgenome.rms.exceptions.AuthenticationNotFoundException
import com.gcgenome.rms.exceptions.ManagerAuthenticationException
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono

@Component
class AuthenticationHandler {
    fun principal(request: ServerRequest): Mono<UserAuthentication> {
        return request.principal().switchIfEmpty(Mono.error(AuthenticationNotFoundException()))
            .cast(UserAuthentication::class.java)
    }
    fun chkUser(request: ServerRequest, userId: String): Mono<UserAuthentication> {
        return request.principal()
            .switchIfEmpty(Mono.error(AuthenticationNotFoundException()))
            .cast(UserAuthentication::class.java)
            .flatMap { userAuthentication ->
                when {
                    userAuthentication.user.id == userId -> Mono.just(userAuthentication)
                    else -> Mono.error(AuthenticationNotFoundException())
                }
            }
    }
    fun chkManager(request: ServerRequest): Mono<UserAuthentication> {
        return request.principal()
            .switchIfEmpty(Mono.error(AuthenticationNotFoundException()))
            .cast(UserAuthentication::class.java)
            .filter { auth -> auth.user.role == Role.MANAGER.toString() || auth.user.role == Role.ADMIN.toString() }
            .switchIfEmpty(Mono.error(ManagerAuthenticationException()))
    }
}