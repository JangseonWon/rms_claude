package com.gcgenome.rms.auth

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.exceptions.AuthenticationNotFoundException
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono

@Component
class AuthenticationHandler {
    fun principal(request: ServerRequest): Mono<UserAuthentication> {
        return request.principal().switchIfEmpty(Mono.error(AuthenticationNotFoundException()))
            .cast(UserAuthentication::class.java)
    }
}