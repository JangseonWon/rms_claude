package com.idrsys.ailis.rms.application.mapper

import com.idrsys.ailis.rms.application.dto.request.CreatePatientCommand
import com.idrsys.ailis.rms.application.dto.request.PatientCommand
import com.idrsys.ailis.rms.application.dto.response.PatientListResponse
import com.idrsys.ailis.rms.application.dto.response.PatientResponse
import com.idrsys.ailis.rms.domain.model.Patient
import com.idrsys.ailis.rms.domain.model.Sex

/**
 * Patient Mapper
 *
 * Patient Domain 모델과 DTO 간의 변환을 담당합니다.
 */
object PatientMapper {

    /**
     * CreatePatientCommand → Patient Domain
     */
    fun toDomain(command: CreatePatientCommand, userId: String): Patient {
        return Patient.create(
            serial = command.serial,
            name = command.name,
            sex = command.sex?.let { Sex.fromCode(it) },
            age = command.age,
            birth = command.birth,
            createdBy = userId
        )
    }

    /**
     * PatientCommand → Patient Domain (의뢰 생성 시)
     */
    fun toDomain(command: PatientCommand, userId: String): Patient {
        return Patient.create(
            serial = command.serial,
            name = command.name,
            sex = command.sex?.let { Sex.fromCode(it) },
            age = command.age,
            birth = command.birth,
            createdBy = userId
        )
    }

    /**
     * Patient Domain → PatientResponse
     */
    fun toResponse(patient: Patient): PatientResponse {
        return PatientResponse(
            id = patient.id!!,
            serial = patient.serial,
            name = patient.name,
            sex = patient.sex?.code,
            age = patient.age,
            birth = patient.birth,
            createdAt = patient.createdAt,
            createdBy = patient.createdBy,
            updatedAt = patient.updatedAt,
            updatedBy = patient.updatedBy
        )
    }

    /**
     * Patient Domain → PatientListResponse
     */
    fun toListResponse(patient: Patient): PatientListResponse {
        return PatientListResponse(
            id = patient.id!!,
            serial = patient.serial,
            name = patient.name,
            sex = patient.sex?.code,
            age = patient.age,
            createdAt = patient.createdAt
        )
    }
}
