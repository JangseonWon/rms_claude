package com.gcgenome.rms.authenticate

import org.bouncycastle.asn1.ASN1Sequence
import org.bouncycastle.asn1.pkcs.RSAPrivateKey
import org.bouncycastle.util.encoders.Base64
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.*
import java.util.regex.Pattern

@Component
data class KeyPair(
    @Value("\${spring.security.oauth2.authorization.jwt.secret}")
    private val jwtSecret: String
) {
    private val pem = Pattern.compile("-----BEGIN (.*)-----(.*)-----END (.*)-----", Pattern.DOTALL)
    val private: PrivateKey = pemToPrivateKey(jwtSecret)

    private fun pemToPrivateKey(pemData: String): PrivateKey {
        val m = pem.matcher(pemData.trim())
        require(m.matches()) { "$pemData is not PEM encoded data" }
        val type = m.group(1)
        val content = Base64.decode(m.group(2).toByteArray(StandardCharsets.UTF_8))
        return when (type) {
            "RSA PRIVATE KEY" -> {
                val seq = ASN1Sequence.getInstance(content)
                require(seq.size() == 9) { "Invalid RSA Private Key ASN1 sequence." }
                val key = RSAPrivateKey.getInstance(seq)
                val privateKeySpec = RSAPrivateCrtKeySpec(
                    key.modulus, key.publicExponent,
                    key.privateExponent, key.prime1, key.prime2, key.exponent1, key.exponent2,
                    key.coefficient
                )
                KeyFactory.getInstance("RSA").generatePrivate(privateKeySpec)
            }else -> throw IllegalArgumentException("$type is not a supported format")
        }
    }
}
