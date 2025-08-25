package com.gcgenome.rms.exception

import com.gcgenome.rms.data.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.util.*

@Component
class ErrorResponseMapper {

    private val log = LoggerFactory.getLogger(ErrorResponseMapper::class.java)

    fun toResponse(e: Throwable, req: ServerRequest): Mono<ServerResponse> {
        val tid = UUID.randomUUID().toString()
        log.error("Error on ${req.method()} ${req.path()} traceId=$tid", e)

        return when (e) {
            is UnprocessableEntityException -> ServerResponse
                .status(e.statusCode)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                    ErrorResponse(
                        status = e.statusCode.value(),
                        path = req.path(),
                        error = e.reason ?: "Validation failed",
                        code = e.code,
                        fieldErrors = if(e.fieldErrors.isEmpty()) null else e.fieldErrors,
                        traceId = tid
                    )
                )

            is ConflictException -> ServerResponse
                .status(e.statusCode)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                    ErrorResponse(
                        status = e.statusCode.value(),
                        path = req.path(),
                        error = e.reason ?: "Conflict",
                        code = e.code,
                        fieldErrors = if(e.fieldErrors.isEmpty()) null else e.fieldErrors,
                        traceId = tid
                    )
                )
            is NotFoundException -> ServerResponse
                .status(e.statusCode)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                    ErrorResponse(
                        status = e.statusCode.value(),
                        path = req.path(),
                        error = e.reason ?: "Not Found",
                        code = e.code,
                        fieldErrors = if (e.fieldErrors.isEmpty()) null else e.fieldErrors,
                        traceId = tid
                    )
                )
            else -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                    ErrorResponse(
                        status = 500,
                        path = req.path(),
                        error = "Please contact Genome",
                        traceId = tid
                    )
                )
        }
    }
}