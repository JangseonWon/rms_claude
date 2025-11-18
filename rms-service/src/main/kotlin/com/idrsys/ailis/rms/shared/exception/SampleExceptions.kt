package com.idrsys.ailis.rms.shared.exception

/**
 * Sample(샘플) 관련 Exception 정의
 */

/**
 * 샘플을 찾을 수 없는 경우
 */
class SampleNotFoundException : NotFoundException {
    constructor() : super("exception.notFound.sample")
    constructor(sampleId: String) : super("exception.notFound.sample.id", sampleId)
}

/**
 * 샘플 유형을 찾을 수 없는 경우
 */
class SampleTypeNotFoundException : NotFoundException {
    constructor() : super("exception.notFound.sampleType")
    constructor(sampleTypeId: String) : super("exception.notFound.sampleType.id", sampleTypeId)

    companion object {
        fun bySerial(serial: String) = NotFoundException(
            "exception.notFound.sampleType.serial",
            serial
        )
    }
}

/**
 * 중복된 샘플 유형이 존재하는 경우
 */
class DuplicateSampleTypeException : DuplicateException(
    "exception.duplicate.sampleType"
)

/**
 * 샘플 수량 검증 실패
 */
class SampleCountValidationException : ValidationException(
    "exception.validation.sample.count"
)

/**
 * 샘플 채취일 검증 실패
 */
class SampleSamplingDateValidationException : ValidationException(
    "exception.validation.sample.samplingDate"
)
