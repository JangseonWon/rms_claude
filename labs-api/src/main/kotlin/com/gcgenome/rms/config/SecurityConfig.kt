package com.gcgenome.rms.config

import com.gcgenome.rms.dao.UserDao_
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import java.util.*

@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
    private val userDao: UserDao_
) {
    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .addFilterAt(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
            .authorizeExchange { exchanges ->
                exchanges
                    .pathMatchers("/labs/**").authenticated()
                    .anyExchange().permitAll()
            }
            .build()
    }

    private fun authenticationWebFilter(): AuthenticationWebFilter {
        val manager = ReactiveAuthenticationManager { authentication ->
            val token = authentication.credentials.toString()
            userDao.selectUserByKey(UUID.fromString(token))
                .map { user ->
                    CustomAuthenticationToken(user)
                }
        }

        val filter = AuthenticationWebFilter(manager)
        filter.setServerAuthenticationConverter(BearerTokenConverter())
        return filter
    }
}