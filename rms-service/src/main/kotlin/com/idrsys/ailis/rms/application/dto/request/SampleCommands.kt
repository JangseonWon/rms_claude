package com.idrsys.ailis.rms.application.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Positive
import org.openapitools.jackson.nullable.JsonNullable
import java.time.LocalDate

/**
 * 샘플 생성 Command
 */
data class CreateSampleCommand(
    @field:NotNull(message = "{validation.notNull}")
    @field:Positive(message = "{validation.positive}")
    val count: Int,

    @field:NotNull(message = "{validation.notNull}")
    @field:PastOrPresent(message = "{validation.pastOrPresent}")
    @JsonProperty("sampling_on")
    val samplingOn: LocalDate,

    @field:Valid
    @field:NotNull(message = "{validation.notNull}")
    val type: SampleTypeCommand,

    val age: Int? = null
)

/**
 * 샘플 수정 Command (전체 업데이트)
 */
data class UpdateSampleCommand(
    @field:NotNull(message = "{validation.notNull}")
    @field:Positive(message = "{validation.positive}")
    val count: Int,

    @field:NotNull(message = "{validation.notNull}")
    @field:PastOrPresent(message = "{validation.pastOrPresent}")
    @JsonProperty("sampling_on")
    val samplingOn: LocalDate,

    @field:Valid
    @field:NotNull(message = "{validation.notNull}")
    val type: SampleTypeCommand,

    val age: Int? = null
)

/**
 * 샘플 부분 수정 Command (PATCH)
 */
data class PatchSampleCommand(
    @JsonProperty("count")
    val count: JsonNullable<@Positive(message = "{validation.positive}") Int> = JsonNullable.undefined(),

    @JsonProperty("age")
    val age: JsonNullable<Int> = JsonNullable.undefined(),

    @JsonProperty("sampling_on")
    val samplingOn: JsonNullable<@PastOrPresent(message = "{validation.pastOrPresent}") LocalDate> = JsonNullable.undefined(),

    @JsonProperty("type")
    val type: JsonNullable<@Valid SampleTypePatchCommand> = JsonNullable.undefined()
)

/**
 * 의뢰에서 사용되는 샘플 정보 Command
 */
data class SampleCommand(
    @field:NotNull(message = "{validation.notNull}")
    @field:Positive(message = "{validation.positive}")
    val count: Int,

    @field:NotNull(message = "{validation.notNull}")
    @field:PastOrPresent(message = "{validation.pastOrPresent}")
    @JsonProperty("sampling_on")
    val samplingOn: LocalDate,

    @field:Valid
    @field:NotNull(message = "{validation.notNull}")
    val type: SampleTypeCommand,

    val age: Int? = null
)

/**
 * 샘플 유형 생성 Command
 */
data class CreateSampleTypeCommand(
    @field:NotBlank(message = "{validation.notBlank}")
    val serial: String,

    @field:NotBlank(message = "{validation.notBlank}")
    val name: String,

    val description: String? = null
)

/**
 * 샘플 유형 수정 Command
 */
data class UpdateSampleTypeCommand(
    @field:NotBlank(message = "{validation.notBlank}")
    val name: String,

    val description: String? = null
)

/**
 * 샘플 유형 부분 수정 Command (PATCH)
 */
data class SampleTypePatchCommand(
    @field:NotBlank(message = "{validation.notBlank}")
    @JsonProperty("serial")
    val serial: JsonNullable<String> = JsonNullable.undefined()
)

/**
 * 샘플에서 사용되는 샘플 유형 Command
 */
data class SampleTypeCommand(
    @field:NotBlank(message = "{validation.notBlank}")
    val serial: String
)
