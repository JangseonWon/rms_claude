package com.gcgenome.rms.login

import com.gcgenome.rms.data.Login_
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.cast

@Configuration
class Router(
    private val handler: Handler
) {
    @Value("\${security.oauth2.authorization.jwt.duration}")
    private var duration: Long = 0

    @Bean("com.gcgenome.rms.login.Router")
    fun route() = router { POST("/api/login", ::login) }

    private fun login(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(Login_::class.java).flatMap(handler::login)
            .map { token -> ResponseCookie.from("Authorization", token).httpOnly(true)/*.secure(true)*/.maxAge(duration).build() }
            .flatMap { cookie -> ServerResponse.ok().cookie(cookie).header("X-USER-ID", "G066661").build() }
            .switchIfEmpty (ServerResponse.status(HttpStatus.NOT_FOUND).build())
            .onErrorResume(Exception::class.java) { ServerResponse.status(HttpStatus.UNAUTHORIZED).build() }
            .doOnError { e -> e.printStackTrace() }
    }
}
