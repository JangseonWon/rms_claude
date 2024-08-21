package com.gcgenome.rms.authentication

import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebExceptionHandler
import reactor.core.publisher.Mono

@Component
@Order(-2)
class AuthenticationExceptionHandler : WebExceptionHandler {
    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        return if(ex is io.jsonwebtoken.ExpiredJwtException || ex is AuthenticationException){
            exchange.response.apply {
                statusCode = HttpStatus.UNAUTHORIZED
            }.setComplete()
        }else{
            Mono.error(ex)
        }
    }
}