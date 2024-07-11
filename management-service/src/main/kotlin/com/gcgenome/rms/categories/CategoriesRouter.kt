package com.gcgenome.rms.categories

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.auth.ManagerAuthenticationHandler
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.exception.*
import com.gcgenome.rms.tables.pojos.Category
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
    private val managerAuthenticationHandler: ManagerAuthenticationHandler,
    private val categoriesHandler: CategoriesHandler
) {
    @Bean("CategoriesRouter")
    fun route() = router {
        GET("/w-api/management-service/categories", ::searchCategories)
        POST("/w-api/management-service/categories/{category_id}/services", ::selectCategories)
        PUT("/w-api/management-service/categories", ::insertCategories)
        DELETE("/w-api/management-service/categories", ::deleteCategories)
        PATCH("/w-api/management-service/categories", ::updateCategories)
    }

    private fun searchCategories(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.principal(request)
            .flatMap { categoriesHandler.searchCategories().collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(CategoryNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume {
                it.printStackTrace()
                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Request body error.") }
    }

    private fun insertCategories(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.principal(request)
            .flatMap { managerAuthenticationHandler.chkManager(it) }
            .flatMap { request.bodyToMono(Category::class.java) }
            .flatMap { categoriesHandler.insertCategories(it) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(ManagerAuthenticationException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(CategoryNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Request body error.") }
    }

    private fun deleteCategories(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.principal(request)
            .flatMap { managerAuthenticationHandler.chkManager(it) }
            .flatMap { request.bodyToMono(Category::class.java) }
            .flatMap { categoriesHandler.deleteCategories(it) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(ManagerAuthenticationException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(CategoryNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Request body error.") }
    }

    private fun updateCategories(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.principal(request)
            .flatMap { managerAuthenticationHandler.chkManager(it) }
            .flatMap { request.bodyToMono(Category::class.java) }
            .flatMap { categoriesHandler.updateCategories(it) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(ManagerAuthenticationException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(CategoryNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Request body error.") }
    }

    private fun selectCategories(request: ServerRequest): Mono<ServerResponse> {
        val categoryId = UUID.fromString(request.pathVariable("category_id"))
        return authenticationHandler.principal(request)
            .flatMap { request.bodyToMono(Query.Companion.Filter::class.java) }
            .flatMap { categoriesHandler.selectServiceInfoByCategoryId(categoryId, it).collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(CategoryNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Request body error.") }
    }
}

