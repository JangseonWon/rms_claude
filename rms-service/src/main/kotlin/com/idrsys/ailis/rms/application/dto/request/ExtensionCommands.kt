package com.idrsys.ailis.rms.application.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

/**
 * 추가 정보 생성 Command
 */
data class CreateExtensionCommand(
    @field:NotBlank(message = "{validation.notBlank}")
    val code: String,

    @field:NotBlank(message = "{validation.notBlank}")
    val name: String,

    val description: String? = null,

    @JsonProperty("data_type")
    val dataType: String = "STRING",

    @JsonProperty("is_required")
    val isRequired: Boolean = false
)

/**
 * 추가 정보 수정 Command
 */
data class UpdateExtensionCommand(
    @field:NotBlank(message = "{validation.notBlank}")
    val name: String,

    val description: String? = null,

    @JsonProperty("data_type")
    val dataType: String = "STRING",

    @JsonProperty("is_required")
    val isRequired: Boolean = false
)

/**
 * 의뢰 추가 정보 Command
 */
data class RequestExtensionCommand(
    @field:NotBlank(message = "{validation.notBlank}")
    val code: String,

    @field:NotBlank(message = "{validation.notBlank}")
    val value: String
)
