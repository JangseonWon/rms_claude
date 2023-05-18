package com.gcgenome.rms.auth

import io.jsonwebtoken.Claims
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.stream.Collectors

object TokenToAuthentication {
    @Suppress("UNCHECKED_CAST")
    fun map(claims: Claims): Authentication {
        val id = claims.get("id", String::class.java)
        val jti = claims.id
        val issuedAt = claims.issuedAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        val notBefore = claims.notBefore.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        val expire = claims.expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        val issuer = claims.issuer
        val audience = claims.audience
        val auths: List<String> =  claims.get("authorities", List::class.java) as List<String>
        val authorities: List<SimpleGrantedAuthority> = auths.stream().map(::SimpleGrantedAuthority).collect(Collectors.toList())
        return UserAuthentication(
            jti,
            authorities,
            id,
            issuer,
            audience,
            issuedAt,
            notBefore,
            expire
        )
    }
    class UserAuthentication(
        val id: String,
        authorities: Collection<GrantedAuthority>,
        private val username: String,
        private val issuer: String,
        private val audience: String,
        private val issuedDateTime: LocalDateTime,
        private val notBeforeDateTime: LocalDateTime,
        val expireDateTime: LocalDateTime): AbstractAuthenticationToken(authorities) {
        init {
            super.setAuthenticated(true)
        }
        override fun getName(): String = username
        override fun getCredentials(): Any = TODO("Not yet implemented")
        override fun getPrincipal(): String = username
    }
}