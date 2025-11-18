package com.idrsys.ailis.rms.adapter.web.handler

import com.idrsys.ailis.rms.shared.exception.BaseException
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import java.time.LocalDateTime

/**
 * 전역 Exception 처리
 *
 * 모든 Exception을 catch하여 일관된 형식의 에러 응답을 반환합니다.
 * i18n 메시지를 사용하여 현재 Locale에 맞는 메시지를 반환합니다.
 */
@RestControllerAdvice
class GlobalExceptionHandler(
    private val messageSource: MessageSource
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * BaseException 처리
     *
     * 비즈니스 로직에서 발생하는 모든 Exception 처리
     */
    @ExceptionHandler(BaseException::class)
    suspend fun handleBaseException(ex: BaseException): ResponseEntity<ErrorResponse> {
        val message = ex.getLocalizedMessage(messageSource)

        logger.warn("BaseException occurred: messageKey={}, args={}, status={}",
            ex.messageKey, ex.args.joinToString(), ex.httpStatus)

        return ResponseEntity
            .status(ex.httpStatus)
            .body(ErrorResponse(
                status = ex.httpStatus.value(),
                error = ex.httpStatus.reasonPhrase,
                message = message,
                path = null,
                timestamp = LocalDateTime.now()
            ))
    }

    /**
     * Validation Exception 처리
     *
     * @Valid, @Validated 어노테이션으로 발생하는 검증 오류 처리
     */
    @ExceptionHandler(WebExchangeBindException::class)
    suspend fun handleValidationException(
        ex: WebExchangeBindException
    ): ResponseEntity<ValidationErrorResponse> {
        val errors = ex.bindingResult.allErrors.map { error ->
            FieldErrorDetail(
                field = (error as? FieldError)?.field ?: "unknown",
                message = error.defaultMessage ?: "Validation error",
                rejectedValue = (error as? FieldError)?.rejectedValue
            )
        }

        logger.warn("Validation failed: {} errors", errors.size)

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ValidationErrorResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                error = HttpStatus.BAD_REQUEST.reasonPhrase,
                message = "입력 값 검증에 실패했습니다",
                errors = errors,
                timestamp = LocalDateTime.now()
            ))
    }

    /**
     * IllegalArgumentException 처리
     *
     * 잘못된 인자가 전달된 경우
     */
    @ExceptionHandler(IllegalArgumentException::class)
    suspend fun handleIllegalArgumentException(
        ex: IllegalArgumentException
    ): ResponseEntity<ErrorResponse> {
        logger.warn("IllegalArgumentException occurred: {}", ex.message)

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                error = HttpStatus.BAD_REQUEST.reasonPhrase,
                message = ex.message ?: "잘못된 요청입니다",
                path = null,
                timestamp = LocalDateTime.now()
            ))
    }

    /**
     * IllegalStateException 처리
     *
     * 잘못된 상태에서 메서드가 호출된 경우
     */
    @ExceptionHandler(IllegalStateException::class)
    suspend fun handleIllegalStateException(
        ex: IllegalStateException
    ): ResponseEntity<ErrorResponse> {
        logger.warn("IllegalStateException occurred: {}", ex.message)

        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ErrorResponse(
                status = HttpStatus.CONFLICT.value(),
                error = HttpStatus.CONFLICT.reasonPhrase,
                message = ex.message ?: "잘못된 상태입니다",
                path = null,
                timestamp = LocalDateTime.now()
            ))
    }

    /**
     * 모든 예외 처리 (Fallback)
     *
     * 위에서 처리되지 않은 모든 예외를 처리
     */
    @ExceptionHandler(Exception::class)
    suspend fun handleException(ex: Exception): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected exception occurred", ex)

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(
                status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                error = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
                message = "서버 내부 오류가 발생했습니다",
                path = null,
                timestamp = LocalDateTime.now()
            ))
    }
}

/**
 * 에러 응답 DTO
 */
data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val path: String?,
    val timestamp: LocalDateTime
)

/**
 * Validation 에러 응답 DTO
 */
data class ValidationErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val errors: List<FieldErrorDetail>,
    val timestamp: LocalDateTime
)

/**
 * 필드별 에러 상세 정보
 */
data class FieldErrorDetail(
    val field: String,
    val message: String,
    val rejectedValue: Any?
)
