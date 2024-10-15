package com.gcgenome.rms.product.sample_type

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.SampleType
import com.gcgenome.rms.exception.AuthenticationNotFoundException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration
class SampleTypeRouter (
    private val handler: SampleTypeHandler,
    private val authentication: AuthenticationHandler
){
    @Bean("SampleTypeRouter")
    fun route() = router {
        GET("/w-api/product-service/sample_types", :: getSampleTypes)
    }

    private fun getSampleTypes(request: ServerRequest): Mono<ServerResponse> {
        val serviceId = request.queryParam("service_id").get()
        return authentication.principal(request)
            .flatMap { handler.getSampleTypes(serviceId).collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), SampleType::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $e")}
    }
}
