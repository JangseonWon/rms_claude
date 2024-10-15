package com.gcgenome.rms.product.organization

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

@Configuration
class OrganizationRouter (
    private val handler: OrganizationHandler,
    private val authentication: AuthenticationHandler
){
    @Bean("OrganizationRouter")
    fun route() = router {
        GET("/w-api/product-service/organizations", :: getOrganizations)
    }

    private fun getOrganizations(request: ServerRequest): Mono<ServerResponse> {
        return authentication.principal(request)
            .flatMap { handler.getOrganizations(it.user.id!!).collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), Organization::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $e")}
    }
}
