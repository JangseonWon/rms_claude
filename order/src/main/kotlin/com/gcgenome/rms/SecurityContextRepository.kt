package com.gcgenome.rms

import com.gcgenome.rms.data.User
import com.gcgenome.rms.order.UserDao
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class SecurityContextRepository(
    private val userDao: UserDao
) : ServerSecurityContextRepository {
    override fun save(exchange: ServerWebExchange, context: SecurityContext): Mono<Void> = Mono.empty()
    override fun load(exchange: ServerWebExchange): Mono<SecurityContext> {
        return Mono.justOrEmpty(exchange.request.headers.getFirst("X-USER-ID"))
            .flatMap (userDao::selectUser)
            .map { u -> SecurityContextImpl(UserAuthentication(u)) }
    }
    class UserAuthentication(val user: User): Authentication {
        override fun getName(): String = user.name
        override fun getAuthorities(): Collection<GrantedAuthority> = emptyList()
        override fun getCredentials(): Any = TODO("Not yet implemented")
        override fun getDetails(): User = user
        override fun getPrincipal(): String = user.id
        override fun isAuthenticated(): Boolean = true
        override fun setAuthenticated(isAuthenticated: Boolean) = TODO("Not yet implemented")
    }
}