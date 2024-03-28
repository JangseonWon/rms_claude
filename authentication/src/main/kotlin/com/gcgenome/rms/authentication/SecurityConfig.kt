package com.gcgenome.rms.authentication

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter

@Configuration
@EnableReactiveMethodSecurity
class SecurityConfig(
    private val jwtAuthenticationConverter: JwtAuthenticationConverter, jwtAuthenticationManager: JwtAuthenticationManager
) {
    private val authenticationWebFilter = AuthenticationWebFilter(jwtAuthenticationManager)
    @Bean
    fun securityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http {
            csrf { disable() }
            httpBasic { disable() }
            formLogin { disable() }
            headers { frameOptions { mode = XFrameOptionsServerHttpHeadersWriter.Mode.SAMEORIGIN } }
            authenticationWebFilter.setServerAuthenticationConverter(jwtAuthenticationConverter)
            addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
        }
    }
}