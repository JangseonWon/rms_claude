package com.gcgenome.rms.authenticate

import com.fasterxml.jackson.databind.ObjectMapper
import com.gcgenome.rms.data.Token
import com.gcgenome.rms.data.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@Service
class TokenFactory(
    private val objectMapper: ObjectMapper,
    @Value("\${security.oauth2.authorization.jwt.signature-algorithm}")
    private val algorithm: String,
    @Value("\${security.oauth2.authorization.jwt.duration}")
    private val duration: Long,
    keyPair: KeyPair
) {
    private val privateKey = keyPair.private
    fun publish(user: User): String {
        val iat = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond()
        val payload = Token(
            nbf = iat,
            exp = iat + duration,
            iss = "request-test.gcgenome.com",
            aud = "request-test.gcgenome.com",
            iat = iat,
            jti = UUID.randomUUID().toString(),
            id = user.id,
            authorities = arrayOf(user.authority!!),
            name = user.name,
            department = user.department
        )
        return sign(payload)
    }
    private fun sign(payload: Token): String {
        val signatureAlgorithm = SignatureAlgorithm.forName(algorithm)
        return Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setPayload(objectMapper.writeValueAsString(payload))
            .signWith(privateKey, signatureAlgorithm)
            .compact()
    }
}