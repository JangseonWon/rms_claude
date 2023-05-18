package com.gcgenome.rms.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.gcgenome.rms.data.Token_
import com.gcgenome.rms.entity.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneId

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
        val payload: Token_ = UserToToken.map(iat, iat + duration, "rms.gcgenome.com", "rms.gcgenome.com", iat, user)
        return sign(payload)
    }
    private fun sign(payload: Token_): String {
        val signatureAlgorithm = SignatureAlgorithm.forName(algorithm)
        return Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setPayload(objectMapper.writeValueAsString(payload))
            .signWith(privateKey, signatureAlgorithm)
            .compact()
    }
}