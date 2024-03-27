package com.gcgenome.rms.request

import com.gcgenome.rms.config.SecurityContextRepository
import com.gcgenome.rms.data.Query
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

@Configuration
class RequestRouter(
    private val requstHandler: RequestHandler
) {
    @Bean
    fun route() = router {
        POST("/w-api/order-service/orders", ::selectRequsts)
    }

    private fun selectRequsts(request: ServerRequest) : Mono<ServerResponse> {
        return request.principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .zipWith(request.bodyToMono(Query::class.java))
            .flatMap { requstHandler.selectRequests(it.t1.principal, it.t2.copy(page= it.t2.page - 1)) }
            .flatMap { request ->
                ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(request)
            }.onErrorResume(DataAccessException::class.java)  {e ->  ColumnNotFoundException(e).toServerResponse()}
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $e")}
    }
}