package com.gcgenome.rms.authenticate

import com.fasterxml.jackson.databind.ObjectMapper
import com.gcgenome.rms.data.Token
import com.gcgenome.rms.tables.pojos.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.SecureDigestAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.PrivateKey
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@Service
class TokenFactory(
    private val objectMapper: ObjectMapper,
    @Value("\${spring.security.oauth2.authorization.jwt.signature-algorithm}")
    private val algorithm: String,
    @Value("\${spring.security.oauth2.authorization.jwt.duration}")
    private val duration: Long,
    keyPair: KeyPair
) {
    private val privateKey = keyPair.private
    fun publish(user: User): String {
        val iat = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond()
        val payload = Token(
            nbf = iat,
            exp = iat + duration,
            iss = "rms-test.gcgenome.com",
            aud = "rms-test.gcgenome.com",
            iat = iat,
            jti = UUID.randomUUID().toString(),
            user = user.apply { password = null }
        )
        return sign(payload)
    }
    private fun sign(payload: Token): String {
        val signatureAlgorithm = Jwts.SIG.get()[algorithm]
        if(signatureAlgorithm is SecureDigestAlgorithm) {
            return Jwts.builder()
                .header().add("typ", "JWT").and()
                .content(objectMapper.writeValueAsString(payload))
                .signWith(privateKey, signatureAlgorithm as SecureDigestAlgorithm<PrivateKey, *>)
                .compact()
        } else throw IllegalArgumentException("Unsupported algorithm: $algorithm")
    }
}