package com.gcgenome.rms.history

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.UserHistoryDTO
import com.gcgenome.rms.exception.AuthenticationNotFoundException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Configuration
class UserHistoryRouter(
    private val userHistoryHandler: UserHistoryHandler,
    private val authenticationHandler: AuthenticationHandler
) {
    @Bean("UserHistoryRouter")
    fun route() = router {
        POST("/w-api/management-service/users-history/search", :: findUserChangedHistory)
    }

    private fun findUserChangedHistory(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.chkManager(request)
            .flatMap { request.bodyToMono(Query::class.java).defaultIfEmpty(Query()) }
            .flatMap { query -> userHistoryHandler.selectUserHistory(query) }
            .flatMap { ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Total-Count", it.totalCount.toString())
                .header("X-Total-Page", it.totalPage.toString())
                .header("X-Page-Size", it.pageSize.toString())
                .header("X-Current-Page", it.currentPage.toString())
                .body(Flux.fromIterable(it.data), UserHistoryDTO::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Please contact Genome") }
    }
}

