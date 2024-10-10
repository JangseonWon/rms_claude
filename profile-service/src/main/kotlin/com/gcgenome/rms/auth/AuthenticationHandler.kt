package com.gcgenome.rms.auth

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.exceptions.AuthenticationNotFoundException
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono

@Component
class AuthenticationHandler {
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
}