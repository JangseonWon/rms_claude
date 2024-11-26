package com.gcgenome.rms.request

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.PatientDTO
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.RequestDTO
import com.gcgenome.rms.data.ServiceDTO
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
class RequestRouter(
    val authenticationHandler: AuthenticationHandler,
    val requestHandler: RequestHandler
) {
    @Bean("RequestRouter")
    fun route() = router {
        POST("/w-api/order-service/requests/search", ::selectRequests)
        GET("/w-api/order-service/services/{service-id}", ::findService)
        POST("/w-api/order-service/patients/search", ::findPatient)
    }
    private fun selectRequests(request: ServerRequest) : Mono<ServerResponse> {
        return authenticationHandler.principal(request).zipWith(request.bodyToMono(Query::class.java))
            .flatMap { requestHandler.selectRequests(it.t1, it.t2) }
            .flatMap { ServerResponse.ok()
                .header("X-Total-Count", it.totalCount.toString())
                .header("X-Total-Page", it.totalPage.toString())
                .header("X-Page-Size", it.pageSize.toString())
                .header("X-Current-Page", it.currentPage.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(it.data), RequestDTO::class.java ) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $e")}
    }
    private fun findService(request: ServerRequest): Mono<ServerResponse> {
        val serviceId = request.pathVariable("service-id")
        return authenticationHandler.principal(request)
            .flatMap { requestHandler.selectService(serviceId).collectList() }
            .flatMap { ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(it), ServiceDTO::class.java)
            }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $e")}
    }
    private fun findPatient(request: ServerRequest) : Mono<ServerResponse> {
        return authenticationHandler.principal(request).zipWith(request.bodyToMono(Query::class.java))
            .flatMap { requestHandler.selectPatients(it.t1, it.t2) }
            .flatMap { ServerResponse.ok()
                .header("X-Total-Count", it.totalCount.toString())
                .header("X-Total-Page", it.totalPage.toString())
                .header("X-Page-Size", it.pageSize.toString())
                .header("X-Current-Page", it.currentPage.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(it.data), PatientDTO::class.java ) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $e")}
    }
}