package com.gcgenome.rms.catalog.categories

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.exceptions.AuthenticationNotFoundException
import com.gcgenome.rms.exceptions.CategoryNotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
class CategoriesRouter (
    private val authenticationHandler: AuthenticationHandler,
    private val categoriesHandler: CategoriesHandler
) {
    @Bean("CategoriesRouter")
    fun route() = router {
        GET("/w-api/catalog-service/categories", ::searchCategories)
        GET("/w-api/catalog-service/categories/{category_id}", ::getCategories)
    }
    private val logger: Logger = LoggerFactory.getLogger(CategoriesRouter::class.java)

    private fun searchCategories(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.principal(request)
            .flatMap { categoriesHandler.searchCategories().collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(CategoryNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume { e ->
                logger.error(e.stackTraceToString())
                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Please contact Genome")
            }
    }

    private fun getCategories(request: ServerRequest): Mono<ServerResponse> {
        val categoryId = UUID.fromString(request.pathVariable("category_id"))
        return authenticationHandler.principal(request)
            .flatMap { categoriesHandler.getCategories(categoryId) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(CategoryNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume { e ->
                logger.error(e.stackTraceToString())
                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Please contact Genome")
            }
    }
}

