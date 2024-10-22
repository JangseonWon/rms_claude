package com.gcgenome.rms.product.extension

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.ServiceExtension
import com.gcgenome.rms.exception.AuthenticationNotFoundException
import com.gcgenome.rms.exception.ServiceNotFoundException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration
class ExtensionRouter (
    private val handler: ExtensionHandler,
    private val authentication: AuthenticationHandler
){
    @Bean("ExtensionRouter")
    fun route() = router {
        GET("/w-api/product-service/services/{serviceId}/extensions", ::serviceExtensions)
    }

    private fun serviceExtensions(request: ServerRequest): Mono<ServerResponse> {
        val serviceId = request.pathVariable("serviceId")
        return authentication.principal(request)
            .flatMap { handler.extensionByServiceId(serviceId).collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), ServiceExtension::class.java)}
            .onErrorResume (AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume (ServiceNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
    }
}