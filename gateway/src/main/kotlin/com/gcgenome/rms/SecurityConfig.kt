package com.gcgenome.rms

import com.gcgenome.rms.auth.AuthenticationManager
import com.gcgenome.rms.auth.SecurityContextRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter
import org.springframework.web.server.ServerWebExchange
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
        return http.cors().and()
            .httpBasic().disable()
            .csrf().disable()
            .formLogin().disable()
            .headers().frameOptions().mode(XFrameOptionsServerHttpHeadersWriter.Mode.SAMEORIGIN).and()
            .exceptionHandling()
            .authenticationEntryPoint { swe: ServerWebExchange, _: AuthenticationException ->
                Mono.fromRunnable { swe.response.statusCode = HttpStatus.UNAUTHORIZED }
            }.accessDeniedHandler { swe: ServerWebExchange, _: AccessDeniedException ->
                Mono.fromRunnable { swe.response.statusCode = HttpStatus.FORBIDDEN }
            }.and().authenticationManager(authenticationManager)
            .securityContextRepository(securityContextRepository)
            .authorizeExchange()
            .pathMatchers(HttpMethod.OPTIONS).permitAll()
            .pathMatchers(HttpMethod.POST,"/api/login").permitAll()
            .pathMatchers(HttpMethod.GET, "/api/test").permitAll()
            .anyExchange().authenticated()
            .and().build()
    }
}