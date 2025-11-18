package com.idrsys.ailis.rms.shared.exception

/**
 * Service(서비스) 관련 Exception 정의
 */

/**
 * 서비스를 찾을 수 없는 경우
 */
class ServiceNotFoundException : NotFoundException {
    constructor() : super("exception.notFound.service")
    constructor(serviceId: String) : super("exception.notFound.service.id", serviceId)

    companion object {
        fun bySerial(serial: String) = NotFoundException(
            "exception.notFound.service.serial",
            serial
        )
    }
}

/**
 * 중복된 서비스가 존재하는 경우
 */
class DuplicateServiceException : DuplicateException {
    constructor() : super("exception.duplicate.service")
    constructor(serial: String) : super("exception.duplicate.service.serial", serial)
}
