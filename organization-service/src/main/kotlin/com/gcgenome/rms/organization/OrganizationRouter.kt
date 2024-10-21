package com.gcgenome.rms.organization

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.PatchOrganization
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.exception.AuthenticationNotFoundException
import com.gcgenome.rms.exception.ColumnNotFoundException
import com.gcgenome.rms.exception.OrganizationNotFoundException
import com.gcgenome.rms.exception.WebInputException
import com.gcgenome.rms.tables.pojos.Organization
import org.jooq.exception.DataAccessException
import org.jooq.exception.IntegrityConstraintViolationException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import org.springframework.web.server.ServerWebInputException
import reactor.core.publisher.Mono

@Configuration
class OrganizationRouter (
    private val organizationHandler: OrganizationHandler,
    private val authenticationHandler: AuthenticationHandler
) {
    @Bean
    fun route() = router {
        /*다 사용 하지 않음*/
        PUT("/w-api/organization-service/organizations", ::insertOrganization)
//        POST("/w-api/organization-service/organizations", ::selectOrganizations)
//        GET("/w-api/organization-service/organizations/{organization_id}", ::getOrganizationById)
//        PATCH("/w-api/organization-service/organizations/{organization_id}", ::updateOrganization)
    }

    private fun insertOrganization(request: ServerRequest) : Mono<ServerResponse> {
        return authenticationHandler.principal(request)
            .zipWith(request.bodyToMono(Organization::class.java))
            .flatMap { organizationHandler.insertOrganization(it.t1.user.id!!, it.t2) }
            .flatMap { organization -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(organization) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume (ServerWebInputException::class.java) { ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(WebInputException().message.toString()) }
            .onErrorResume (IntegrityConstraintViolationException::class.java) {ServerResponse.status(HttpStatus.CONFLICT).bodyValue("Duplicate key error")}
    }

    private fun getOrganizationById(request: ServerRequest) : Mono<ServerResponse> {
        val id = request.pathVariable("organization_id")
        return authenticationHandler.principal(request)
            .flatMap { organizationHandler.getOrganizationById(id) }
            .flatMap { organization -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(organization) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume (OrganizationNotFoundException::class.java) { ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${it.message}") }
    }

    private fun updateOrganization(request: ServerRequest) : Mono<ServerResponse> {
        val organizationId = request.pathVariable("organization_id")
        return authenticationHandler.principal(request)
            .then(request.bodyToMono(PatchOrganization::class.java))
            .flatMap { organizationHandler.updateOrganization(it.apply { id = organizationId }) }
            .flatMap { organization -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(organization) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume (ServerWebInputException::class.java) { ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(WebInputException().message.toString()) }
    }

    private fun selectOrganizations(request: ServerRequest): Mono<ServerResponse> {
        return authenticationHandler.principal(request)
            .zipWith(request.bodyToMono(Query::class.java))
            .flatMap {
                organizationHandler.selectOrganizations(it.t1.user.id!!, it.t2.copy(page= it.t2.page - 1)) }
            .flatMap { organizations ->
                ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                    .header("X-Total-Page", organizations.totalPage.toString())
                    .bodyValue(organizations)
            }.onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(DataAccessException::class.java)  {e ->  ColumnNotFoundException(e).toServerResponse()}
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("오류코드: $e")}
    }
}
