package com.idrsys.ailis.rms.application.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive
import org.openapitools.jackson.nullable.JsonNullable
import java.time.LocalDate

/**
 * 환자 생성 Command
 */
data class CreatePatientCommand(
    @field:NotBlank(message = "{validation.notBlank}")
    val serial: String,

    @field:NotBlank(message = "{validation.notBlank}")
    val name: String,

    @field:Pattern(regexp = "^[MF]$", message = "{validation.pattern.sex}")
    val sex: String? = null,

    @field:Positive(message = "{validation.positive}")
    val age: Int? = null,

    @field:PastOrPresent(message = "{validation.pastOrPresent}")
    val birth: LocalDate? = null
)

/**
 * 환자 수정 Command (전체 업데이트)
 */
data class UpdatePatientCommand(
    @field:NotBlank(message = "{validation.notBlank}")
    val name: String,

    @field:Pattern(regexp = "^[MF]$", message = "{validation.pattern.sex}")
    val sex: String? = null,

    @field:Positive(message = "{validation.positive}")
    val age: Int? = null,

    @field:PastOrPresent(message = "{validation.pastOrPresent}")
    val birth: LocalDate? = null
)

/**
 * 환자 부분 수정 Command (PATCH)
 */
data class PatchPatientCommand(
    @JsonProperty("name")
    val name: JsonNullable<String> = JsonNullable.undefined(),

    @JsonProperty("sex")
    val sex: JsonNullable<@Pattern(regexp = "^[MF]$", message = "{validation.pattern.sex}") String> = JsonNullable.undefined(),

    @JsonProperty("age")
    val age: JsonNullable<@Positive(message = "{validation.positive}") Int> = JsonNullable.undefined(),

    @JsonProperty("birth")
    val birth: JsonNullable<@PastOrPresent(message = "{validation.pastOrPresent}") LocalDate> = JsonNullable.undefined()
)

/**
 * 의뢰에서 사용되는 환자 정보 Command
 */
data class PatientCommand(
    @field:NotBlank(message = "{validation.notBlank}")
    val serial: String,

    @field:NotBlank(message = "{validation.notBlank}")
    val name: String,

    @field:Pattern(regexp = "^[MF]$", message = "{validation.pattern.sex}")
    val sex: String? = null,

    val age: Int? = null,

    val birth: LocalDate? = null
)
