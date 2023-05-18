package com.gcgenome.rms.auth

import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import org.springframework.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.*

@Component
class SecurityContextRepository(
    private val authenticationManager: AuthenticationManager,
    keyPair: KeyPair
) : ServerSecurityContextRepository {
    private val parser: JwtParser = Jwts.parserBuilder().setSigningKey(keyPair.public).build()

    override fun save(exchange: ServerWebExchange, context: SecurityContext) = throw UnsupportedOperationException("Not supported yet.")
    override fun load(exchange: ServerWebExchange): Mono<SecurityContext> {
        val request = exchange.request
        val cookie = request.cookies.getFirst("Authorization")
        val authHeader: String? = if(cookie!=null && cookie.value.isNotEmpty()) cookie.value else request.headers.getFirst(HttpHeaders.AUTHORIZATION)
        return if (authHeader != null) {
            authentication(authHeader.substring(authHeader.indexOf(" ") + 1))
                .map<Mono<SecurityContext>> { authentication -> this.authenticationManager.authenticate(authentication).map(::SecurityContextImpl) }
                .orElse(Mono.empty())
        } else Mono.empty()
    }
    fun authentication(token: String): Optional<Authentication> {
        return try {
            val claims = parser.parseClaimsJws(token).body
            Optional.ofNullable(TokenToAuthentication.map(claims))
        } catch (e: Exception) {
            Optional.empty()
        }
    }
}