package com.idrsys.ailis.rms.application.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import org.openapitools.jackson.nullable.JsonNullable

/**
 * 기관 생성 Command
 */
data class CreateOrganizationCommand(
    @field:NotBlank(message = "{validation.notBlank}")
    val serial: String,

    @field:NotBlank(message = "{validation.notBlank}")
    val name: String,

    @JsonProperty("registration_number")
    val registrationNumber: String? = null,

    @JsonProperty("nursing_number")
    val nursingNumber: String? = null,

    @JsonProperty("branch_code")
    val branchCode: String? = null,

    @JsonProperty("branch_name")
    val branchName: String? = null,

    @JsonProperty("employee_id")
    val employeeId: String? = null,

    @JsonProperty("employee_name")
    val employeeName: String? = null,

    @JsonProperty("employee_phone")
    val employeePhone: String? = null,

    val type: String? = null
)

/**
 * 기관 수정 Command (전체 업데이트)
 */
data class UpdateOrganizationCommand(
    @field:NotBlank(message = "{validation.notBlank}")
    val name: String,

    @JsonProperty("registration_number")
    val registrationNumber: String? = null,

    @JsonProperty("nursing_number")
    val nursingNumber: String? = null,

    @JsonProperty("branch_code")
    val branchCode: String? = null,

    @JsonProperty("branch_name")
    val branchName: String? = null,

    @JsonProperty("employee_id")
    val employeeId: String? = null,

    @JsonProperty("employee_name")
    val employeeName: String? = null,

    @JsonProperty("employee_phone")
    val employeePhone: String? = null,

    val type: String? = null
)

/**
 * 기관 부분 수정 Command (PATCH)
 */
data class PatchOrganizationCommand(
    @JsonProperty("name")
    val name: JsonNullable<String> = JsonNullable.undefined(),

    @JsonProperty("registration_number")
    val registrationNumber: JsonNullable<String> = JsonNullable.undefined(),

    @JsonProperty("nursing_number")
    val nursingNumber: JsonNullable<String> = JsonNullable.undefined(),

    @JsonProperty("branch_code")
    val branchCode: JsonNullable<String> = JsonNullable.undefined(),

    @JsonProperty("branch_name")
    val branchName: JsonNullable<String> = JsonNullable.undefined(),

    @JsonProperty("employee_id")
    val employeeId: JsonNullable<String> = JsonNullable.undefined(),

    @JsonProperty("employee_name")
    val employeeName: JsonNullable<String> = JsonNullable.undefined(),

    @JsonProperty("employee_phone")
    val employeePhone: JsonNullable<String> = JsonNullable.undefined(),

    @JsonProperty("type")
    val type: JsonNullable<String> = JsonNullable.undefined()
)

/**
 * 의뢰에서 사용되는 기관 정보 Command
 */
data class OrganizationCommand(
    @field:NotBlank(message = "{validation.notBlank}")
    val serial: String
)
