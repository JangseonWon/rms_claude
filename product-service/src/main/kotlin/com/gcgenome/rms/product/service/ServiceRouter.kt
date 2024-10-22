package com.gcgenome.rms.product.service

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.Organization
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
class ServiceRouter (
    private val handler: ServiceHandler,
    private val authentication: AuthenticationHandler
){
    @Bean("ServiceRouter")
    fun route() = router {
        GET("/w-api/product-service/services", :: getServices)
    }

    private fun getServices(request: ServerRequest): Mono<ServerResponse> {
        val categoryId = UUID.fromString(request.queryParam("category_id").get())
        return authentication.principal(request)
            .flatMap { handler.getServices(it, categoryId).collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), Organization::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $e")}
    }
}
