package com.idrsys.ailis.rms.shared.exception

/**
 * Organization(기관) 관련 Exception 정의
 */

/**
 * 기관을 찾을 수 없는 경우
 */
class OrganizationNotFoundException : NotFoundException {
    constructor() : super("exception.notFound.organization")
    constructor(organizationId: String) : super("exception.notFound.organization.id", organizationId)

    companion object {
        fun bySerial(serial: String) = NotFoundException(
            "exception.notFound.organization.serial",
            serial
        )
    }
}

/**
 * 중복된 기관이 존재하는 경우
 */
class DuplicateOrganizationException : DuplicateException {
    constructor() : super("exception.duplicate.organization")
    constructor(serial: String) : super("exception.duplicate.organization.serial", serial)
}

/**
 * 의뢰가 존재하는 기관을 삭제하려는 경우
 */
class OrganizationHasRequestException : ValidationException(
    "exception.delete.organization.hasRequest"
)
