package com.gcgenome.rms.catalog.patient

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.PatientDTO
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.exceptions.AuthenticationNotFoundException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration
class PatientRouter (
    private val authenticationHandler: AuthenticationHandler,
    private val patientHandler: PatientHandler
) {
    @Bean("PatientRouter")
    fun route() = router {
        POST("/w-api/catalog-service/patients/search", ::findPatient)
    }

    private fun findPatient(request: ServerRequest) : Mono<ServerResponse> {
        return authenticationHandler.principal(request).zipWith(request.bodyToMono(Query::class.java))
            .flatMap { patientHandler.selectPatients(it.t1, it.t2) }
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

