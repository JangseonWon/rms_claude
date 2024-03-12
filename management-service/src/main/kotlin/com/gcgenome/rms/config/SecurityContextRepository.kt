package com.gcgenome.rms.config

import com.gcgenome.rms.dao.UserDao
import com.gcgenome.rms.data.User
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.util.Logger

@Component
class SecurityContextRepository(
    private val dslContext: DSLContext
) : ServerSecurityContextRepository, UserDao {
    val logger = LoggerFactory.getLogger(this.javaClass)
    override fun save(exchange: ServerWebExchange, context: SecurityContext): Mono<Void> = Mono.empty()
    override fun load(exchange: ServerWebExchange): Mono<SecurityContext> {
        val userId = "240004"
//        return Mono.justOrEmpty(exchange.request.headers.getFirst("X-USER-ID"))
        return Mono.justOrEmpty(userId)
            .flatMap { dslContext.selectUserById(it)}
            .map { u -> SecurityContextImpl(UserAuthentication(u)) }
    }
    class UserAuthentication(val user: User): Authentication {
        override fun getName(): String = user.name!!
        override fun getAuthorities(): Collection<GrantedAuthority> {
            val authorities = user.role.split(",") // Assuming authorities are comma-separated
            return authorities.map { UserGrantedAuthority(it) }
        }
        override fun getCredentials() = TODO("Not yet implemented")
        override fun getDetails() = TODO("Not yet implemented")
        override fun getPrincipal(): String = user.id
        override fun isAuthenticated(): Boolean = user.state.equals("ACTIVE")
        override fun setAuthenticated(isAuthenticated: Boolean) = TODO("Not yet implemented")
    }
}