package com.gcgenome.rms.config

import com.gcgenome.rms.auth.AuthenticationManager
import com.gcgenome.rms.auth.SecurityContextRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.ServerAuthenticationEntryPoint
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers
import reactor.core.publisher.Mono

@Configuration
@Order(2)
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@EnableR2dbcAuditing
class SecurityConfig (
    private val authenticationManager: AuthenticationManager,
    private val securityContextRepository: SecurityContextRepository
) {
    @Bean
    fun resourceFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http.authenticationManager(authenticationManager)
            .securityContextRepository(securityContextRepository)
        return http {
            cors {  }
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
                authorize(pathMatchers(HttpMethod.OPTIONS, "/**"), permitAll)
                authorize(pathMatchers(HttpMethod.GET,"/actuator/health/**","/api/test", "/"),permitAll)
                authorize(pathMatchers(HttpMethod.POST, "/api/user/login"),permitAll)
                authorize(anyExchange, authenticated)
            }
        }
    }
}