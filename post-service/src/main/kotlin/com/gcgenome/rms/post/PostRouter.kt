package com.gcgenome.rms.post

import com.fasterxml.jackson.databind.ObjectMapper
import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.data.JandiRequest
import com.gcgenome.rms.data.PostDTO
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.UserDTO
import com.gcgenome.rms.exception.AuthenticationNotFoundException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.codec.multipart.FormFieldPart
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import java.util.*

@Configuration
class PostRouter (
    private val authenticationHandler: AuthenticationHandler,
    private val serviceHandler: PostHandler,
    private val objectMapper: ObjectMapper,
) {
    @Bean("PostRouter")
    fun route() = router {
        POST("/w-api/post-service/post/search", ::selectPosts)
        GET("/w-api/post-service/post/{post-id}", ::selectPostById)
        PUT("/w-api/post-service/posts", ::insertPost)
        DELETE("/w-api/post-service/post/{post-id}", ::deletePost)
        PATCH("/w-api/post-service/post/{post-id}", ::updatePost)
        /*PUT("/w-api/post-service/post/{post_id}", ::postIdCheckSwitch)
        POST("/w-api/post-service/post/{post_id}/message/{category}", ::jandiWebHook)*/
    }

    private fun selectPosts(request: ServerRequest): Mono<ServerResponse> {
        val category = request.queryParam("category").get()
        return Mono.zip(authenticationHandler.principal(request), request.bodyToMono(Query::class.java))
            .flatMap { serviceHandler.selectPosts(it.t1, it.t2, category) }
            .flatMap { ServerResponse.ok()
                .header("X-Total-Count", it.totalCount.toString())
                .header("X-Total-Page", it.totalPage.toString())
                .header("X-Page-Size", it.pageSize.toString())
                .header("X-Current-Page", it.currentPage.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(it.data), PostDTO::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error: ${it.cause}") }
    }

    private fun selectPostById(request: ServerRequest): Mono<ServerResponse> {
        val postId = UUID.fromString(request.pathVariable("post-id"))
        return authenticationHandler.principal(request)
            .flatMap { serviceHandler.selectPost(postId, it) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), PostDTO::class.java) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error: ${it.cause}") }
    }

    private fun insertPost(request: ServerRequest): Mono<ServerResponse> {
        return request.multipartData()
            .flatMap { parts ->
                val fileParts = parts["file"]?.filterIsInstance<FilePart>() ?: emptyList()
                val jsonPart = parts["data"]?.filterIsInstance<FormFieldPart>()?.firstOrNull()?.value() ?: ""
                val postDTO = objectMapper.readValue(jsonPart, PostDTO::class.java)
                Mono.zip(
                    authenticationHandler.principal(request),
                    Mono.just(fileParts),
                    Mono.just(postDTO)
                )
            }.flatMap {
                val auth = it.t1
                val fileParts = it.t2
                val postDTO = it.t3
                postDTO.user = UserDTO(id = auth.user.id)
                serviceHandler.insertPost(postDTO, fileParts)
            }
            .flatMap { ServerResponse.ok().build() }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error: ${it.cause}") }
    }

    private fun updatePost(request: ServerRequest): Mono<ServerResponse> {
        val postId = UUID.fromString(request.pathVariable("post-id"))
        return request.multipartData()
            .flatMap { parts ->
                val fileParts = parts["file"]?.filterIsInstance<FilePart>() ?: emptyList()
                val jsonPart = parts["data"]?.filterIsInstance<FormFieldPart>()?.firstOrNull()?.value() ?: ""
                val postDTO = objectMapper.readValue(jsonPart, PostDTO::class.java)
                Mono.zip(
                    authenticationHandler.principal(request),
                    Mono.just(fileParts),
                    Mono.just(postDTO)
                )
            }.flatMap {
                val auth = it.t1
                val fileParts = it.t2
                val postDTO = it.t3.apply { id = postId }
                postDTO.user = UserDTO(id = auth.user.id)
                serviceHandler.updatePost(postDTO, fileParts)
            }
            .flatMap { ServerResponse.ok().build() }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error: ${it.cause}") }
    }

    private fun deletePost(request: ServerRequest): Mono<ServerResponse> {
        val postId = UUID.fromString(request.pathVariable("post-id"))
        return authenticationHandler.principal(request)
            .flatMap { serviceHandler.deletePost(postId) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("error: ${it.cause}") }
    }

    private fun postIdCheckSwitch(request: ServerRequest): Mono<ServerResponse> {
        val postId = UUID.fromString(request.pathVariable("post_id"))
        return authenticationHandler.principal(request)
            .flatMap { request.bodyToMono(Boolean::class.java) }
            .flatMap { serviceHandler.postIdCheckSwitch(postId, it) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Request body error.") }
    }

    private fun jandiWebHook(request: ServerRequest): Mono<ServerResponse> {
        val postId = UUID.fromString(request.pathVariable("post_id"))
        val category = request.pathVariable("category")
        return authenticationHandler.principal(request)
            .flatMap { request.bodyToMono(JandiRequest::class.java) }
            .flatMap { serviceHandler.sendToJandi(postId, category, it) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Request body error.") }
    }
}

