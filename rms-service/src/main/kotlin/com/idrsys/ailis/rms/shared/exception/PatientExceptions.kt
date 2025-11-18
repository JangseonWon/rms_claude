package com.idrsys.ailis.rms.shared.exception

/**
 * Patient(환자) 관련 Exception 정의
 */

/**
 * 환자를 찾을 수 없는 경우
 */
class PatientNotFoundException : NotFoundException {
    constructor() : super("exception.notFound.patient")
    constructor(patientId: String) : super("exception.notFound.patient.id", patientId)

    companion object {
        fun bySerial(serial: String) = NotFoundException(
            "exception.notFound.patient.serial",
            serial
        )
    }
}

/**
 * 중복된 환자 정보가 존재하는 경우
 */
class DuplicatePatientException : DuplicateException {
    constructor() : super("exception.duplicate.patient")
    constructor(serial: String) : super("exception.duplicate.patient.serial", serial)
}
