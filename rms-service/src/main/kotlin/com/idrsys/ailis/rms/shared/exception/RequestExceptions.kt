package com.idrsys.ailis.rms.shared.exception

/**
 * Request(의뢰) 관련 Exception 정의
 */

/**
 * 의뢰를 찾을 수 없는 경우
 */
class RequestNotFoundException(requestId: String? = null) : NotFoundException(
    if (requestId != null) "exception.notFound.request.id" else "exception.notFound.request",
    *(if (requestId != null) arrayOf(requestId) else emptyArray())
)

/**
 * 중복된 의뢰가 존재하는 경우
 */
class DuplicateRequestException(serial: String? = null) : DuplicateException(
    if (serial != null) "exception.duplicate.request.serial" else "exception.duplicate.request",
    *(if (serial != null) arrayOf(serial) else emptyArray())
)

/**
 * 의뢰 날짜 범위 검증 실패
 */
class RequestDateRangeValidationException : ValidationException(
    "exception.validation.request.dateRange"
)

/**
 * 필수 의뢰 정보 누락
 */
class RequestRequiredFieldException : ValidationException(
    "exception.validation.request.required"
)
