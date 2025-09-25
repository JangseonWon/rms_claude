package com.gcgenome.rms.request.dto.response

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.gcgenome.rms.organization.dto.response.OrganizationResponseDTO
import com.gcgenome.rms.service.dto.response.ServiceResponseDTO
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class RequestResponseDTO(
    @JsonProperty("id") val id: UUID,
    @JsonProperty("department") val department: String? = null,
    @JsonProperty("ward") val ward: String? = null,
    @JsonProperty("physician") val physician: String? = null,
    @JsonProperty("memo") val memo: String? = null,
    @JsonProperty("genomePrice") val genomePrice: Long? = null,
    @JsonProperty("labsPrice") val labsPrice: Long? = null,
    @JsonProperty("createAt") val createAt: LocalDateTime,
    @JsonIgnore val isDeletable: Boolean,
    @JsonIgnore val isEditable: Boolean,
    @JsonIgnore val serviceId: UUID,
    @JsonIgnore val sampleId: UUID,
    @JsonIgnore val organizationId: UUID,
    @JsonIgnore val patientId: UUID,
    var organization: OrganizationResponseDTO? = null,
    var patient: PatientResponseDTO? = null,
    var service: ServiceResponseDTO? = null,
    var sample: RequestSampleDTO? = null,
    var extensions: List<RequestExtensionDTO>? = null
)

data class RequestExtensionDTO(
    @JsonIgnore val id: UUID? = null,
    @JsonProperty("value") val value: String,
    @field:NotBlank(message = "must not be blank")
    @JsonProperty("code") val code: String,
    @JsonProperty("name_kr")val nameKr: String? = null,
    @JsonProperty("name_en")val nameEn: String? = null,
    @JsonIgnore var extensionId: UUID? = null ,
    @JsonIgnore var requestId: UUID? = null,
)

data class RequestSampleDTO(
    @JsonIgnore val id: UUID,
    @JsonProperty("barcode") val barcode: String? = null,
    @field:NotBlank(message = "must not be blank")
    @JsonProperty("serial")val serial: String,
    @field:NotBlank(message = "must not be blank")
    @field:Positive(message = "must be > 0")
    @JsonProperty("count")val count: Int,
    @field:NotBlank(message = "must not be blank")
    @JsonProperty("sampling_on")val samplingOn: LocalDate,

    @JsonIgnore var sampleTypeId: UUID,
    @JsonIgnore var userId: UUID,
    @JsonProperty("type") var type: SampleTypeResponseDTO? = null

)

data class SampleTypeResponseDTO(
    @JsonIgnore val id: UUID?,
    @field:NotBlank(message = "must not be blank")
    @JsonProperty("code") val code: String,
    @field:NotBlank(message = "must not be blank")
    @JsonProperty("serial") val serial: String,
    @JsonProperty("name_kr") val nameKr: String? = null,
    @JsonProperty("name_en") val nameEn: String? = null
)

data class PatientResponseDTO(
    @JsonIgnore val id: UUID,
    @field:NotBlank(message = "must not be blank")
    @JsonProperty("serial") val serial: String,
    @field:NotBlank(message = "must not be blank")
    @JsonProperty("name") val name: String,
    @JsonProperty("sex") val sex: String? = null,
    @JsonProperty("birth") val birth: LocalDate? = null,
    @JsonProperty("age") val age: Int? = null
)