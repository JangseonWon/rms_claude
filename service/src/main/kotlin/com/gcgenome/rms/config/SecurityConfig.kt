package com.gcgenome.rms.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.data.domain.ReactiveAuditorAware
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.ServerAuthenticationEntryPoint
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.time.Duration

@Configuration
@Order(2)
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@EnableR2dbcAuditing
class SecurityConfig (
    private val securityContextRepository: SecurityContextRepository
) {
    @Bean
    fun resourceFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http.securityContextRepository(securityContextRepository)
        return http {
            cors { }
            httpBasic { disable() }
            csrf { disable() }
            formLogin { disable() }
            headers { frameOptions { mode = XFrameOptionsServerHttpHeadersWriter.Mode.SAMEORIGIN } }
            exceptionHandling {
                authenticationEntryPoint = ServerAuthenticationEntryPoint { exchange, _ ->
                    Mono.fromRunnable { exchange.response.statusCode = HttpStatus.UNAUTHORIZED }
                }
                accessDeniedHandler = ServerAccessDeniedHandler { exchange, _ ->
                    Mono.fromRunnable { exchange.response.statusCode = HttpStatus.FORBIDDEN }
                }
            }
            authorizeExchange {
                authorize (ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET, "/actuator/health/**"), permitAll)
                authorize (anyExchange, authenticated)
            }
        }
    }
}