package com.gcgenome.rms.categories

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.exception.AuthenticationNotFoundException
import com.gcgenome.rms.exception.CategoryNotFoundException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration
class CategoriesRouter (
    private val authenticationHandler: AuthenticationHandler,
    private val categoriesHandler: CategoriesHandler
) {
    @Bean("CategoriesRouter")
    fun route() = router {
        GET("/w-api/management-service/categories", ::searchCategories)
    }

    private fun searchCategories(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.principal(request)
            .flatMap { categoriesHandler.searchCategories().collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(CategoryNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume { e ->  ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: ${e}") }
    }
}

