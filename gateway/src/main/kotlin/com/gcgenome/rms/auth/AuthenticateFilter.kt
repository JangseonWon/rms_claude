package com.gcgenome.rms.auth

import com.gcgenome.rms.data.User
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.http.ResponseCookie
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Component
class AuthenticateFilter(
    private val securityContextRepository: SecurityContextRepository,
    private val tokenFactory: TokenFactory,
    private val dslContext: DSLContext
) : AbstractGatewayFilterFactory<Any>(), Dao {
    @Value("\${security.oauth2.authorization.jwt.duration}")
    private val duration: Long = 0

    override fun apply(config: Any): GatewayFilter {
        return OrderedGatewayFilter({ exchange, chain ->
                securityContextRepository.load(exchange)
                    .map { obj -> obj.authentication }
                    .switchIfEmpty(Mono.just(TestingAuthenticationToken(null, null))) // 실패할 Authentication
                    .flatMap { auth -> exchange(exchange, chain, auth) }
            }, 80)
    }

    private fun exchange(exchange: ServerWebExchange, chain: GatewayFilterChain, auth: Authentication): Mono<Void> {
        var exchange = exchange
        if (auth.isAuthenticated && auth.principal != null) {
            val request = exchange.request.mutate().header("X-USER-ID", auth.principal.toString()).build()
            exchange = exchange.mutate().request(request).build()
            if (auth is TokenToAuthentication.UserAuthentication) {
                val cast: TokenToAuthentication.UserAuthentication = auth
                if (cast.expireDateTime.minusMinutes(5).isBefore(LocalDateTime.now())) {
                    return chain.filter(exchange)
                        .then(dslContext.selectUser(cast.principal).map(User::toDto))
                        .map(tokenFactory::publish)
                        .map { token -> ResponseCookie.from("Authorization", token).httpOnly(true).secure(true).maxAge(duration).build() }
                        .doOnNext { cookie -> exchange.response.addCookie(cookie) }
                        .then()
                }
            }
        }
        return chain.filter(exchange)
    }
}