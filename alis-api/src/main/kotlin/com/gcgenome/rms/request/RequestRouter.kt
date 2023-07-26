package com.gcgenome.rms.request

import com.gcgenome.rms.data.Body
import com.gcgenome.rms.data.DateFormatException
import com.gcgenome.rms.data.XMLQuery

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import java.math.BigInteger
import java.security.KeyFactory
import java.security.spec.RSAPrivateKeySpec
import java.util.*
import javax.crypto.Cipher
import kotlin.text.Charsets.UTF_8

@Configuration
class RequestRouter(
    private var handler: RequestHandler,
    @Value("\${greencross.alis.private.modulus}")
    private val modulus: String,
    @Value("\${greencross.alis.private.exponent}")
    private val exponent: String
) {
    private val cipher: Cipher = Cipher.getInstance("RSA")
    private val decoder = Base64.getDecoder()
    init {
        val modulusByteArray2 = Base64.getDecoder().decode(modulus)
        val exponentByteArray2 = Base64.getDecoder().decode(exponent)
        val privateKeySpec = RSAPrivateKeySpec(BigInteger(1, modulusByteArray2), BigInteger(1, exponentByteArray2))
        val factory = KeyFactory.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, factory.generatePrivate(privateKeySpec))
    }
    @Bean("ServiceRouterLegacy")
    fun route() = router {
        POST("/api/alis/requests", contentType(MediaType("application", "vnd.alis-api.v0", UTF_8)), ::requests)
    }

    private fun requests(request: ServerRequest): Mono<ServerResponse> {
        val version = request.headers().contentType().get().subtype
        return request.bodyToMono(XMLQuery::class.java)
            .filter {
                if(it.account != null) {
                    val id = decode(it.account.id)
                    val pwd = decode(it.account.pwd)
                    id=="alis" && pwd=="alis1234"
                } else false
            }.flatMap {handler.resolve(Mono.just(it), version)}
            .flatMap(ServerResponse.ok()::bodyValue)
            .switchIfEmpty(ServerResponse.status(HttpStatus.NO_CONTENT).build())
            .onErrorResume(DateFormatException::class.java) {
                ServerResponse.badRequest().contentType(MediaType.APPLICATION_XML).bodyValue(Body(error = Body.Companion.Error(it.message!!)))
            }
    }
    private fun decode(encode: String): String {
        return String(cipher.doFinal(decoder.decode(encode)))
    }
}