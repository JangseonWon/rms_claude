package com.gcgenome.rms.authenticate

import org.bouncycastle.asn1.ASN1Sequence
import org.bouncycastle.asn1.pkcs.RSAPrivateKey
import org.bouncycastle.asn1.pkcs.RSAPublicKey
import org.bouncycastle.util.encoders.Base64
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.*
import java.util.regex.Pattern

@Component
data class KeyPair(
    @Value("\${security.oauth2.authorization.jwt.secret}")
    private val jwtSecret: String
) {
    private val PEM_DATA = Pattern.compile("-----BEGIN (.*)-----(.*)-----END (.*)-----", Pattern.DOTALL)
    final val public: PublicKey
    final val private: PrivateKey?
    init {
        val pair = parseKeyPair(jwtSecret)
        public = pair.first
        private = pair.second
    }

    private fun parseKeyPair(pemData: String): Pair<PublicKey, PrivateKey?> {
        val m = PEM_DATA.matcher(pemData.trim { it <= ' ' })
        require(m.matches()) { "String is not PEM encoded data" }
        val type = m.group(1)
        val content = Base64.decode(m.group(2).toByteArray(StandardCharsets.UTF_8))
        val publicKey: PublicKey
        val privateKey: PrivateKey?
        return try {
            val fact = KeyFactory.getInstance("RSA")
            when (type) {
                "RSA PRIVATE KEY" -> {
                    val seq = ASN1Sequence.getInstance(content)
                    require(seq.size() == 9) { "Invalid RSA Private Key ASN1 sequence." }
                    val key = RSAPrivateKey.getInstance(seq)
                    val pubSpec = RSAPublicKeySpec(key.modulus, key.publicExponent)
                    val privSpec = RSAPrivateCrtKeySpec(
                        key.modulus, key.publicExponent,
                        key.privateExponent, key.prime1, key.prime2, key.exponent1, key.exponent2,
                        key.coefficient
                    )
                    publicKey = fact.generatePublic(pubSpec)
                    privateKey = fact.generatePrivate(privSpec)
                }
                "PUBLIC KEY" -> {
                    val keySpec: KeySpec = X509EncodedKeySpec(content)
                    publicKey = fact.generatePublic(keySpec)
                    privateKey = null
                }
                "RSA PUBLIC KEY" -> {
                    val seq = ASN1Sequence.getInstance(content)
                    val key = RSAPublicKey.getInstance(seq)
                    val pubSpec = RSAPublicKeySpec(key.modulus, key.publicExponent)
                    publicKey = fact.generatePublic(pubSpec)
                    privateKey = null
                } else -> throw IllegalArgumentException("$type is not a supported format")
            }
            Pair(publicKey, privateKey)
        } catch (e: InvalidKeySpecException) {
            throw RuntimeException(e)
        } catch (e: NoSuchAlgorithmException) {
            throw IllegalStateException(e)
        }
    }
}
