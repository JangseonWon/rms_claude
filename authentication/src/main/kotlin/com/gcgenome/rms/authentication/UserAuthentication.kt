package com.gcgenome.rms.authentication

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Claims
import org.springframework.security.authentication.AbstractAuthenticationToken
import java.time.LocalDateTime
import java.time.ZoneId

data class UserAuthentication (
    val id: String,
    val user: User,
    val issuer: String,
    val audience: Set<String>,
    val issuedDateTime: LocalDateTime,
    val notBeforeDateTime: LocalDateTime,
    val expireDateTime: LocalDateTime,
    private val token: String
): AbstractAuthenticationToken(emptySet()) {
    constructor(claims: Claims, token: String): this(
        id = claims.id,
        user = convertClaimsToUser(claims),
        issuer = claims.issuer,
        audience = claims.audience,
        issuedDateTime = claims.issuedAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
        notBeforeDateTime = claims.notBefore.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
        expireDateTime = claims.expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
        token = token
    )
    override fun getCredentials(): String = token
    override fun getPrincipal(): String = id
    companion object {
        private val objectMapper = ObjectMapper()

        private fun convertClaimsToUser(claims: Claims): User {
            val userData = claims["user"]
            return objectMapper.convertValue(userData, User::class.java)
        }
    }
}