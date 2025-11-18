package com.idrsys.ailis.rms.shared.exception

import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus

/**
 * 애플리케이션 기본 Exception
 *
 * 모든 비즈니스 Exception은 이 클래스를 상속받아야 합니다.
 * i18n 메시지를 지원하며, HTTP 상태 코드를 포함합니다.
 *
 * @property messageKey i18n 메시지 키
 * @property args 메시지 포맷 인자
 * @property httpStatus HTTP 상태 코드
 */
sealed class BaseException(
    val messageKey: String,
    vararg val args: Any,
    val httpStatus: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
) : RuntimeException() {

    /**
     * 현재 Locale에 맞는 메시지 반환
     */
    fun getLocalizedMessage(messageSource: MessageSource): String {
        return messageSource.getMessage(
            messageKey,
            args,
            LocaleContextHolder.getLocale()
        )
    }

    override val message: String
        get() = "[$messageKey] ${args.joinToString(", ")}"
}

// ============================================
// Common Exceptions
// ============================================

/**
 * 리소스를 찾을 수 없는 경우 (404 Not Found)
 */
abstract class NotFoundException(
    messageKey: String,
    vararg args: Any
) : BaseException(messageKey, *args, httpStatus = HttpStatus.NOT_FOUND)

/**
 * 중복된 리소스가 존재하는 경우 (409 Conflict)
 */
abstract class DuplicateException(
    messageKey: String,
    vararg args: Any
) : BaseException(messageKey, *args, httpStatus = HttpStatus.CONFLICT)

/**
 * 입력 값 검증 실패 (422 Unprocessable Entity)
 */
abstract class ValidationException(
    messageKey: String,
    vararg args: Any
) : BaseException(messageKey, *args, httpStatus = HttpStatus.UNPROCESSABLE_ENTITY)

/**
 * 인증 실패 (401 Unauthorized)
 */
abstract class AuthenticationException(
    messageKey: String,
    vararg args: Any
) : BaseException(messageKey, *args, httpStatus = HttpStatus.UNAUTHORIZED)

/**
 * 권한 없음 (403 Forbidden)
 */
abstract class AuthorizationException(
    messageKey: String,
    vararg args: Any
) : BaseException(messageKey, *args, httpStatus = HttpStatus.FORBIDDEN)

/**
 * 잘못된 요청 (400 Bad Request)
 */
abstract class BadRequestException(
    messageKey: String,
    vararg args: Any
) : BaseException(messageKey, *args, httpStatus = HttpStatus.BAD_REQUEST)

/**
 * 데이터베이스 제약 조건 위반 (409 Conflict)
 */
abstract class DatabaseException(
    messageKey: String,
    vararg args: Any
) : BaseException(messageKey, *args, httpStatus = HttpStatus.CONFLICT)
