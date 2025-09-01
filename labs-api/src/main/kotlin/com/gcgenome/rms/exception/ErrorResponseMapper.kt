package com.gcgenome.rms.exception

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.gcgenome.rms.data.ErrorResponse
import com.gcgenome.rms.data.FieldError
import org.slf4j.LoggerFactory
import org.springframework.core.codec.DecodingException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.server.ServerWebInputException
import reactor.core.publisher.Mono
import java.util.*

@Component
class ErrorResponseMapper {

    private val log = LoggerFactory.getLogger(ErrorResponseMapper::class.java)

    fun toResponse(e: Throwable, req: ServerRequest): Mono<ServerResponse> {
        val tid = UUID.randomUUID().toString()
        log.error("Error on ${req.method()} ${req.path()} traceId=$tid", e)

        if (e is ServerWebInputException) {
            val cause = findCause<JsonMappingException>(e)
            val (field, message, code: ErrorCode) = when (cause) {
                is MismatchedInputException -> Triple(
                    extractJsonPath(cause),
                    "Missing required field",
                    ErrorCode.BAD_REQUEST
                )
                is JsonMappingException -> Triple(
                    extractJsonPath(cause),
                    "JSON mapping error",
                    ErrorCode.BAD_REQUEST
                )
                else -> {
                    val jp = findCause<JsonParseException>(e)
                    if (jp != null) Triple(null, "Malformed JSON", ErrorCode.BAD_REQUEST)
                    else Triple(null, "JSON decoding error", ErrorCode.BAD_REQUEST)
                }
            }

            val fieldErrors = field?.let { listOf(FieldError(it, message)) }

            return ServerResponse.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                    ErrorResponse(
                        status = 400,
                        path = req.path(),
                        error = message,
                        code = code,
                        fieldErrors = fieldErrors,
                        traceId = tid
                    )
                )
        }

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
    private fun extractJsonPath(ex: JsonMappingException): String =
        ex.path.joinToString(".") { ref ->
            ref.fieldName ?: "[${ref.index}]"
        }

    private inline fun <reified T : Throwable> findCause(t: Throwable): T? {
        var cur: Throwable? = t
        while (cur != null) {
            if (cur is T) return cur
            cur = cur.cause
        }
        return null
    }
}