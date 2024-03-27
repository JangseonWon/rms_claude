package com.gcgenome.rms.service

import com.gcgenome.rms.config.SecurityContextRepository
import com.gcgenome.rms.data.ServiceExtension
import com.gcgenome.rms.exceptions.CategoryNotFoundException
import com.gcgenome.rms.exceptions.ServiceNotFoundException
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
class ExtensionRouter (private val handler: ExtensionHandler){
    @Bean("ExtensionServiceRouter")
    fun route() = router {
        GET("/w-api/product-service/services/{serviceId}/extensions", ::serviceExtensions)
        GET("/w-api/product-service/categories/{categoryId}/extensions", ::categoryExtensions)
    }

    private fun serviceExtensions(request: ServerRequest): Mono<ServerResponse> {
        val serviceId = request.pathVariable("serviceId")
        return request
            .principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .flatMap { handler.extensionByServiceId(serviceId).collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), ServiceExtension::class.java)}
            .onErrorResume (ServiceNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
    }

    private fun categoryExtensions(request: ServerRequest): Mono<ServerResponse> {
        val categoryId = request.pathVariable("categoryId")
        return request
            .principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .flatMap { handler.extensionByCategoryId(UUID.fromString(categoryId)).collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), ServiceExtension::class.java)}
            .onErrorResume (CategoryNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume (IllegalArgumentException::class.java) { ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("Please check the reqeust url")}
    }
}