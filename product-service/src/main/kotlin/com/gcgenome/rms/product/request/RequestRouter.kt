package com.gcgenome.rms.product.request

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.Request
import com.gcgenome.rms.exception.AuthenticationNotFoundException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import java.util.*

@Configuration
class RequestRouter (
    private val handler: RequestHandler,
    private val authentication: AuthenticationHandler
){
    @Bean("RequestRouter")
    fun route() = router {
        PUT("/w-api/product-service/requests", :: saveRequests)
    }

    private fun saveRequests(request: ServerRequest): Mono<ServerResponse> {
        return authentication.principal(request)
            .zipWith(request.bodyToMono(Array<Request>::class.java))
            .flatMap { handler.saveRequest(it.t1.user, it.t2).collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).build() }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $e")}
    }
}
