package com.gcgenome.rms.request.dto.response

import com.fasterxml.jackson.annotation.JsonIgnore
import com.gcgenome.rms.service.dto.response.ServiceResponseDTO
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class RequestResponseDTO(
    val id: UUID,
    val department: String? = null,
    val ward: String? = null,
    val physician: String? = null,
    val genomePrice: Long? = null,
    val labsPrice: Long? = null,
    val createAt: LocalDateTime,
    val organization: OrganizationResponseDTO,
    val patient: PatientResponseDTO,
    val service: ServiceResponseDTO
)

data class OrganizationResponseDTO(
    @JsonIgnore
    val id: UUID,
    val serial: String,
    val name: String,
    val registrationNumber: String? = null,
    val nursingNumber: String? = null,
    val branchCode: String? = null,
    val branchName: String? = null,
    val employeeId: String? = null,
    val employeeName: String? = null,
    val employeePhone: String? = null,
    val type: String? = null,
    val createAt: LocalDateTime? = null,
    @JsonIgnore
    val userId: LocalDateTime? = null,
)

data class PatientResponseDTO(
    @JsonIgnore
    val id: UUID,
    val serial: String? = null,
    val name: String? = null,
    val sex: String? = null,
    val birth: LocalDate? = null,
    val age: Int? = null
)



data class ExtensionResponseDTO(
    val id: UUID,
    val code: String,
    val nameKr: String? = null,
    val nameEn: String? = null,
    val regex: String? = null
)
