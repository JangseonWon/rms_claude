package com.gcgenome.rms.postfile

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.exception.AuthenticationNotFoundException
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
class PostFileRouter (
    private val authenticationHandler: AuthenticationHandler,
    private val postFileHandler: PostFileHandler
) {
    @Bean("PostFileRouter")
    fun route() = router {
        GET("/w-api/post-service/post-files/{file-id}", ::downloadPostFile)
        DELETE("/w-api/post-service/post-files/{file-id}", ::deletePostFile)
    }
    private fun downloadPostFile(request: ServerRequest): Mono<ServerResponse> {
        val fileId = UUID.fromString(request.pathVariable("file-id"))
        return authenticationHandler.principal(request)
            .flatMap { postFileHandler.downloadByFileId(fileId) }
            .flatMap { ServerResponse.ok().contentType(determineMimeType(it.first))
                    .header("Content-Disposition", "attachment; filename=\"${it.first}\"")
                    .bodyValue(it.second) }
            .switchIfEmpty(ServerResponse.status(HttpStatus.NO_CONTENT).build())
            .onErrorResume(AuthenticationNotFoundException::class.java) { e ->
                ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}") }
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Error code: $e") }
    }

    private fun deletePostFile(request: ServerRequest): Mono<ServerResponse> {
        val fileId = UUID.fromString(request.pathVariable("file-id"))
        return authenticationHandler.principal(request)
            .flatMap { postFileHandler.deletePostFileById(fileId) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Request body error.") }
    }

    private fun determineMimeType(filename: String): MediaType {
        val extension = filename.substringAfterLast('.', "").lowercase()
        return when (extension) {
            "pdf" -> MediaType.APPLICATION_PDF
            "png" -> MediaType.IMAGE_PNG
            "jpg", "jpeg" -> MediaType.IMAGE_JPEG
            "gif" -> MediaType.IMAGE_GIF
            "txt" -> MediaType.TEXT_PLAIN
            "html" -> MediaType.TEXT_HTML
            "xml", "xlsx" -> MediaType.APPLICATION_XML
            "json" -> MediaType.APPLICATION_JSON
            else -> MediaType.APPLICATION_OCTET_STREAM
        }
    }
}

