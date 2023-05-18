package com.gcgenome.rms

import com.gcgenome.rms.entity.User
import com.gcgenome.rms.repo.UserRepository
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
    private val repo: UserRepository
) : ServerSecurityContextRepository {
    override fun save(exchange: ServerWebExchange, context: SecurityContext): Mono<Void> = Mono.empty()
    override fun load(exchange: ServerWebExchange): Mono<SecurityContext> {
        return Mono.justOrEmpty(exchange.request.headers.getFirst("X-USER-ID"))
            .flatMap(repo::findById)
            .map { u -> SecurityContextImpl(UserAuthentication(u)) }
    }
    class UserAuthentication(val entity: User): Authentication {
        override fun getName(): String = entity.name
        override fun getAuthorities(): Collection<GrantedAuthority> = emptyList()
        override fun getCredentials(): Any = TODO("Not yet implemented")
        override fun getDetails(): User = entity
        override fun getPrincipal(): String = entity.id
        override fun isAuthenticated(): Boolean = true
        override fun setAuthenticated(isAuthenticated: Boolean) = TODO("Not yet implemented")
    }
}