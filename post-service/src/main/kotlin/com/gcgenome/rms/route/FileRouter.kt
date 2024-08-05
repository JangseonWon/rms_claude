package com.gcgenome.rms.route

import com.gcgenome.rms.auth.AuthenticationHandler
import com.gcgenome.rms.exceptions.AuthenticationNotFoundException
import com.gcgenome.rms.service.FileHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Configuration
class FileRouter (
    private val authenticationHandler: AuthenticationHandler,
    private val serviceHandler: FileHandler
) {
    @Bean("FileRouter")
    fun route() = router {
        GET("/w-api/post-service/post/{post_id}/file/{file_id}", ::downloadFile)
        POST("/w-api/post-service/post/{post_id}/file", ::uploadFile)
        DELETE("/w-api/post-service/post/{post_id}/file/{file_id}", ::deleteFile)
        DELETE("/w-api/post-service/post/{post_id}/file", ::deletePostFileAll)
    }
    private fun downloadFile(request: ServerRequest): Mono<ServerResponse> {
        val postId = UUID.fromString(request.pathVariable("post_id"))
        val fileId = UUID.fromString(request.pathVariable("file_id"))
        return authenticationHandler.principal(request)
            .flatMap { serviceHandler.downloadByFileId(postId, fileId) }
            .flatMap { fileData ->
                val filename = fileData.first
                val mimeType = determineMimeType(filename)
                ServerResponse.ok().contentType(mimeType)
                    .header("Content-Disposition", "attachment; filename=\"$filename\"")
                    .bodyValue(fileData.second)
            }
            .switchIfEmpty(ServerResponse.status(HttpStatus.NO_CONTENT).build())
            .onErrorResume(AuthenticationNotFoundException::class.java) { e ->
                ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")
            }
            .onErrorResume { e ->
                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Error code: $e")
            }
    }

    private fun uploadFile(request: ServerRequest): Mono<ServerResponse> {
        val postId = UUID.fromString(request.pathVariable("post_id"))

        return authenticationHandler.principal(request)
            .flatMap { authentication ->
                request.multipartData()
                    .flatMap { parts ->
                        val fileParts = parts["file"]?.filterIsInstance<FilePart>() ?: emptyList()
                        if (fileParts.isNotEmpty()) {
                            val uploadFlux = Flux.fromIterable(fileParts)
                                .flatMap { filePart -> serviceHandler.uploadFile(authentication, postId, filePart) }
                                .collectList()
                            uploadFlux
                                .flatMap { ServerResponse.ok().bodyValue("Files uploaded successfully") }
                                .onErrorResume { e ->
                                    ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Error code: $e")
                                }
                        } else { ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("No files uploaded") }
                    }
            }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}") }
            .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Error code: $e") }
    }

    private fun deleteFile(request: ServerRequest): Mono<ServerResponse> {
        val postId = UUID.fromString(request.pathVariable("post_id"))
        val fileId = UUID.fromString(request.pathVariable("file_id"))
        return authenticationHandler.principal(request)
            .flatMap { serviceHandler.deleteFileById(postId, fileId) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
            .onErrorResume(AuthenticationNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("${e.message}")}
            .onErrorResume(IllegalArgumentException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Request body error.") }
    }

    private fun deletePostFileAll(request: ServerRequest): Mono<ServerResponse> {
        val postId = UUID.fromString(request.pathVariable("post_id"))
        return authenticationHandler.principal(request)
            .flatMap { serviceHandler.deleteDirectoryByPostId(postId) }
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

