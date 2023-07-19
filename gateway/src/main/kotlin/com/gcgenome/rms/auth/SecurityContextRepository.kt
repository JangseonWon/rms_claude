package com.gcgenome.rms.auth

import com.gcgenome.rms.data.User
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import org.jooq.DSLContext
import org.springframework.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.*

@Component
class SecurityContextRepository(
    val authenticationManager: AuthenticationManager,
    val dslContext: DSLContext,
    keyPair: KeyPair
) : ServerSecurityContextRepository, Dao {
    private val parser: JwtParser = Jwts.parserBuilder().setSigningKey(keyPair.public).build()

    override fun save(exchange: ServerWebExchange, context: SecurityContext) = throw UnsupportedOperationException("Not supported yet.")
    override fun load(exchange: ServerWebExchange): Mono<SecurityContext> {
        val request = exchange.request
        val authHeader = request.cookies.getFirst("Authorization")?.value ?: request.headers.getFirst(HttpHeaders.AUTHORIZATION)
        return if (authHeader != null) {
            val uuidPattern = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}".toRegex()
            if(uuidPattern.matches(authHeader)) {
                dslContext.selectUserByKey(UUID.fromString(authHeader))
                    .map(User::toDto)
                    .map { SecurityContextImpl(UserAuthentication(it)) }
            } else{
                authentication(authHeader.substringAfter(" "))
                    .map<Mono<SecurityContext>> { authentication ->
                        this.authenticationManager.authenticate(authentication).map(::SecurityContextImpl)
                    }.orElse(Mono.empty())
            }
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