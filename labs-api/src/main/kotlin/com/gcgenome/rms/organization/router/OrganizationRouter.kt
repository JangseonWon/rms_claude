package com.gcgenome.rms.organization.router

import com.gcgenome.rms.config.CustomAuthenticationToken
import com.gcgenome.rms.data.FieldError
import com.gcgenome.rms.exception.ErrorResponseMapper
import com.gcgenome.rms.exception.UnprocessableEntityException
import com.gcgenome.rms.organization.dto.request.OrganizationPatchDTO
import com.gcgenome.rms.organization.dto.request.OrganizationPostDTO
import com.gcgenome.rms.organization.dto.request.OrganizationPutDTO
import com.gcgenome.rms.organization.dto.response.OrganizationResponseDTO
import com.gcgenome.rms.organization.handler.OrganizationHandler
import jakarta.validation.Validator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration
class OrganizationRouter (
    private val organizationHandler: OrganizationHandler,
    private val errorResponseMapper: ErrorResponseMapper,
    private val validator: Validator
){
    @Bean
    fun organizationRoute() = router {
        accept(MediaType.parseMediaType("application/vnd.gcgenome.v1+json")).nest {
            contentType(MediaType.APPLICATION_JSON).nest {
                PUT("/labs/organizations/{organization-serial}", ::saveOrganization)
                PATCH("/labs/organizations/{organization-serial}", ::patchOrganization)
                POST("/labs/organizations/search", ::searchOrganizations)
            }
            GET("/labs/organizations/{organization-serial}", :: getOrganizationBySerial)
            DELETE("/labs/organizations/{organization-serial}", :: deleteOrganization)
        }
    }

    private fun saveOrganization(request: ServerRequest): Mono<ServerResponse> {
        val organizationSerial = request.pathVariable("organization-serial")
        val principalMono = request.principal().cast(CustomAuthenticationToken::class.java)
        val bodyMono = request.bodyToMono(OrganizationPutDTO::class.java)
            .switchIfEmpty(Mono.error(UnprocessableEntityException(listOf(FieldError("_body", "must not be empty")))))
            .doOnNext { validateOrThrow(validator, it) }

        return Mono.zip(principalMono, bodyMono)
            .flatMap { tuple -> organizationHandler.saveOrganization(tuple.t1.user.id, tuple.t2, organizationSerial) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), OrganizationResponseDTO::class.java::class.java) }
            .onErrorResume { e -> errorResponseMapper.toResponse(e, request) }
    }

    private fun getOrganizationBySerial(request: ServerRequest): Mono<ServerResponse> {
        val organizationSerial = request.pathVariable("organization-serial")

        return request.principal().cast(CustomAuthenticationToken::class.java)
            .flatMap { token -> organizationHandler.getOrganizationBySerial(token.user.id, organizationSerial) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), OrganizationResponseDTO::class.java) }
            .onErrorResume { e -> errorResponseMapper.toResponse(e, request) }
    }
    private fun patchOrganization(request: ServerRequest): Mono<ServerResponse> {
        val principalMono = request.principal().cast(CustomAuthenticationToken::class.java)
        val bodyMono = request.bodyToMono(OrganizationPatchDTO::class.java)
        val organizationSerial = request.pathVariable("organization-serial")

        return Mono.zip(principalMono, bodyMono)
            .flatMap { tuple ->
                val userId = tuple.t1.user.id
                val reqOrganizationPatch = tuple.t2
                organizationHandler.patchOrganization(userId, organizationSerial, reqOrganizationPatch)
            }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume { e -> errorResponseMapper.toResponse(e, request) }
    }
    private fun searchOrganizations(request: ServerRequest): Mono<ServerResponse> {
        val principalMono = request.principal().cast(CustomAuthenticationToken::class.java)
        val bodyMono = request.bodyToMono(OrganizationPostDTO::class.java).defaultIfEmpty(OrganizationPostDTO())

        return Mono.zip(principalMono, bodyMono)
            .flatMap { tuple -> organizationHandler.searchOrganization(tuple.t1.user.id, tuple.t2).collectList() }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), OrganizationResponseDTO::class.java) }
            .onErrorResume { e -> errorResponseMapper.toResponse(e, request) }
    }

    private fun deleteOrganization(request: ServerRequest): Mono<ServerResponse> {
        val organizationSerial = request.pathVariable("organization-serial")

        return request.principal().cast(CustomAuthenticationToken::class.java)
            .flatMap { token -> organizationHandler.deleteOrganizationBySerial(token.user.id, organizationSerial) }
            .then(ServerResponse.noContent().build())
            .onErrorResume { e -> errorResponseMapper.toResponse(e, request) }
    }
    private fun <T : Any> validateOrThrow(validator: Validator, target: T) {
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
