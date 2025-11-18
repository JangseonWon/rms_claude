package com.idrsys.ailis.rms.shared.exception

/**
 * User(사용자) 관련 Exception 정의
 */

/**
 * 사용자를 찾을 수 없는 경우
 */
class UserNotFoundException : NotFoundException {
    constructor() : super("exception.notFound.user")
    constructor(userId: String) : super("exception.notFound.user.id", userId)
}

/**
 * 중복된 사용자가 존재하는 경우
 */
class DuplicateUserException : DuplicateException(
    "exception.duplicate.user"
)

/**
 * Admin 권한 필요
 */
class AdminAuthorizationException : AuthorizationException(
    "exception.authorization.admin"
)

/**
 * Manager 권한 필요
 */
class ManagerAuthorizationException : AuthorizationException(
    "exception.authorization.manager"
)

/**
 * 인증 필요
 */
class AuthenticationRequiredException : AuthenticationException(
    "exception.authentication.required"
)

/**
 * 인증 실패
 */
class AuthenticationFailedException : AuthenticationException(
    "exception.authentication.failed"
)
