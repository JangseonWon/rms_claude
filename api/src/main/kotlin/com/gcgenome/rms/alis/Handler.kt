package com.gcgenome.rms.alis

import com.gcgenome.rms.alis.dao.RequestDao
import com.gcgenome.rms.alis.data.AlisQuery
import com.gcgenome.rms.alis.data.Body
import com.gcgenome.rms.exceptions.AuthenticationNotFoundException
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.math.BigInteger
import java.security.KeyFactory
import java.security.spec.RSAPrivateKeySpec
import java.util.*
import javax.crypto.Cipher


@Service("com.gcgenome.rms.order.Handler")
class Handler(
    @Value("\${alis.private.modulus}")
    private val modulus: String,
    @Value("\${alis.private.exponent}")
    private val exponent: String,
    val dslContext: DSLContext
): RequestDao {
    private val cipher: Cipher = Cipher.getInstance("RSA")
    private val decoder = Base64.getDecoder()
    init {
        val modulusByteArray2 = Base64.getDecoder().decode(modulus)
        val exponentByteArray2 = Base64.getDecoder().decode(exponent)
        val privateKeySpec = RSAPrivateKeySpec(BigInteger(1, modulusByteArray2), BigInteger(1, exponentByteArray2))
        val factory = KeyFactory.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, factory.generatePrivate(privateKeySpec))
    }
    fun getRequests(alisQuery: AlisQuery): Mono<Body> {
        return Mono.from(dslContext.run {
            checkUser(alisQuery).then(selectBody(alisQuery))
        })
    }
    fun checkUser(alisQuery: AlisQuery): Mono<Void>{
        return Mono.zip(decode(alisQuery.account.id), decode(alisQuery.account.pwd))
            .flatMap {
                if (it.t1 == "alis" && it.t2 == "alis1234") {
                    Mono.empty()
                } else {
                    Mono.error(AuthenticationNotFoundException())

                }
            }
    }
    fun decode(encode: String): Mono<String>{
        return try{
            val decodedString = String(cipher.doFinal(decoder.decode(encode)))
            Mono.just(decodedString)
        }catch (e: Exception) {
            Mono.error(AuthenticationNotFoundException())
        }
    }

}