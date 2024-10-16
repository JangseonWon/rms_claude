package com.gcgenome.rms.dashboard

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.RequestDTO
import com.gcgenome.rms.exception.AuthenticationNotFoundException
import com.gcgenome.rms.exception.ColumnNotFoundException
import org.jooq.exception.DataAccessException
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
class Router(
    private val handler: Handler,
    private val authenticationHandler: AuthenticationHandler
) {

    @Bean("DashBoardRouter")
    fun route() = router {
        GET("/w-api/dashboard-service/statistics", ::selectStatus)
        POST("/w-api/dashboard-service/requests", ::selectRequests)
    }

    private fun selectStatus(request: ServerRequest) : Mono<ServerResponse> {
        return authenticationHandler.principal(request)
            .flatMap { handler.selectStatus(it.user) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(DataAccessException::class.java)  { e ->  ColumnNotFoundException(e).toServerResponse()}
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $e")}
    }
    private fun selectRequests(request: ServerRequest) : Mono<ServerResponse> {
        var status = request.queryParam("status")
        if (!status.isPresent)
            status = Optional.of("ALL")
        return authenticationHandler.principal(request).zipWith(request.bodyToMono(Query::class.java))
            .flatMap { handler.selectRequests(it.t1, it.t2, status.get()) }
            .flatMap { ServerResponse.ok()
                .header("X-Total-Count", it.totalCount.toString())
                .header("X-Total-Page", it.totalPage.toString())
                .header("X-Page-Size", it.pageSize.toString())
                .header("X-Current-Page", it.currentPage.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(it.data), RequestDTO::class.java ) }
            .onErrorResume(DataAccessException::class.java)  {e ->  ColumnNotFoundException(e).toServerResponse()}
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $e")}
    }
}