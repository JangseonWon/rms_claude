package com.gcgenome.rms.service

import com.gcgenome.rms.config.SecurityContextRepository
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
class ServiceRouter(
    private val serviceHandler: ServiceHandler
){

    @Bean("ServiceRouter")
    fun route() = router() {
        GET("/w-api/product-service/categories/{category_id}/services", :: selectService)
    }

    private fun selectService(request: ServerRequest): Mono<ServerResponse> {
        val categoryId= request.pathVariable("category_id")
        return request.principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .flatMap { serviceHandler.selectService(UUID.fromString(categoryId), it.user.id).collectList() }
            .flatMap { service -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(service) }
            .onErrorResume(IllegalArgumentException::class.java) { ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("category id error") }
    }
}