package com.gcgenome.rms.request

import com.gcgenome.rms.config.CustomAuthenticationToken
import com.gcgenome.rms.data.*
import com.gcgenome.rms.exception.ErrorResponseMapper
import com.gcgenome.rms.exception.UnprocessableEntityException
import com.gcgenome.rms.request.dto.request.RequestPatchDTO
import com.gcgenome.rms.request.dto.request.RequestPutDTO
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration
class RequestRouter (
    private val requestHandler: RequestHandler,
    private val errorResponseMapper: ErrorResponseMapper,
    private val validator: jakarta.validation.Validator
){
    @Bean
    fun requestRoute() = router {
        accept(MediaType.parseMediaType("application/vnd.gcgenome.v1+json")).nest {
            contentType(MediaType.APPLICATION_JSON).nest {
                PUT("/labs/requests/{service-serial}/{sample-serial}", ::saveRequest)
                PATCH("/labs/requests/{service-serial}/{sample-serial}", ::patchRequest)
                POST("/labs/requests/search", ::searchRequests)
            }
            GET("/labs/requests/{service-serial}/{sample-serial}", :: getRequestBySerial)
            DELETE("/labs/requests/{service-serial}/{sample-serial}", :: deleteRequestBySerial)
        }

    }
    private fun saveRequest(request: ServerRequest): Mono<ServerResponse> {
        val principalMono = request.principal().cast(CustomAuthenticationToken::class.java)
        val requestBody = request.bodyToMono(RequestPutDTO::class.java)
            .switchIfEmpty(Mono.error(UnprocessableEntityException(listOf(FieldError("_body", "must not be empty")))))
            .doOnNext { validateOrThrow(validator, it) }
        val serviceSerial = request.pathVariable("service-serial")
        val sampleSerial = request.pathVariable("sample-serial")

        return Mono.zip(principalMono, requestBody)
            .flatMap { tuple -> requestHandler.saveRequest(tuple.t1.user.id, tuple.t2, serviceSerial, sampleSerial) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), RequestDTO::class.java) }
            .onErrorResume { e -> errorResponseMapper.toResponse(e, request) }
    }

    private fun getRequestBySerial(request: ServerRequest): Mono<ServerResponse> {
        val serviceSerial = request.pathVariable("service-serial")
        val sampleSerial = request.pathVariable("sample-serial")

        return request.principal().cast(CustomAuthenticationToken::class.java)
            .flatMap { requestHandler.getRequestBySerial(it.user.id, serviceSerial, sampleSerial) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), RequestDTO::class.java) }
            .onErrorResume { e -> errorResponseMapper.toResponse(e, request) }
    }

    private fun deleteRequestBySerial(request: ServerRequest): Mono<ServerResponse> {
        val serviceSerial = request.pathVariable("service-serial")
        val sampleSerial = request.pathVariable("sample-serial")

        return request.principal().cast(CustomAuthenticationToken::class.java)
            .flatMap { requestHandler.deleteRequestBySerial(it.user.id, serviceSerial, sampleSerial) }
            .then(ServerResponse.noContent().build())
            .onErrorResume { e -> errorResponseMapper.toResponse(e, request) }
    }

    private fun searchRequests(request: ServerRequest): Mono<ServerResponse> {
        val principalMono = request.principal().cast(CustomAuthenticationToken::class.java)
        val bodyMono = request.bodyToMono(RequestSearchDTO::class.java).defaultIfEmpty(RequestSearchDTO())

        return Mono.zip(principalMono, bodyMono)
            .flatMap { requestHandler.searchRequests(it.t1.user.id, it.t2).collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), RequestDTO::class.java) }
            .onErrorResume { e -> errorResponseMapper.toResponse(e, request) }
    }
    private fun patchRequest(request: ServerRequest): Mono<ServerResponse> {
        val principalMono = request.principal().cast(CustomAuthenticationToken::class.java)
        val serviceSerial = request.pathVariable("service-serial")
        val sampleSerial  = request.pathVariable("sample-serial")
        val bodyMono = request.bodyToMono(RequestPatchDTO::class.java)

        return Mono.zip(principalMono, bodyMono)
            .flatMap { tuple ->
                val userId = tuple.t1.user.id
                val reqRequestPatch = tuple.t2
                requestHandler.patchRequest(userId, serviceSerial, sampleSerial, reqRequestPatch)
            }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume { e -> errorResponseMapper.toResponse(e, request) }
    }

    private fun <T : Any> validateOrThrow(validator: jakarta.validation.Validator, target: T) {
        val v = validator.validate(target)
        if (v.isNotEmpty()) {
            val errors = v.map {
                FieldError(
                    field = it.propertyPath.toString(),
                    message = it.message
                )
            }
            throw UnprocessableEntityException(errors)
        }
    }
}
