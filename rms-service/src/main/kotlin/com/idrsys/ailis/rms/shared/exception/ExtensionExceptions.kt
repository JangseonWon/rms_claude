package com.idrsys.ailis.rms.shared.exception

/**
 * Extension(추가 정보) 관련 Exception 정의
 */

/**
 * 추가 정보를 찾을 수 없는 경우
 */
class ExtensionNotFoundException : NotFoundException {
    constructor() : super("exception.notFound.extension")
    constructor(extensionId: String) : super("exception.notFound.extension.id", extensionId)

    companion object {
        fun byCode(code: String) = NotFoundException(
            "exception.notFound.extension.code",
            code
        )
    }
}

/**
 * 중복된 추가 정보가 존재하는 경우
 */
class DuplicateExtensionException : DuplicateException(
    "exception.duplicate.extension"
)
