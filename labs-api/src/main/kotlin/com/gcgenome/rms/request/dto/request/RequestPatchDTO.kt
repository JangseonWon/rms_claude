package com.gcgenome.rms.request.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import org.openapitools.jackson.nullable.JsonNullable

data class RequestPatchDTO(
    @JsonProperty("department")   val department: JsonNullable<String> = JsonNullable.undefined(),
    @JsonProperty("ward")         val ward: JsonNullable<String> = JsonNullable.undefined(),
    @JsonProperty("physician")    val physician: JsonNullable<String> = JsonNullable.undefined(),
    @JsonProperty("memo")         val memo: JsonNullable<String> = JsonNullable.undefined(),
    @JsonProperty("genome_price") val genomePrice: JsonNullable<Long> = JsonNullable.undefined(),
    @JsonProperty("labs_price")   val labsPrice: JsonNullable<Long> = JsonNullable.undefined(),

    @JsonProperty("organization") val organization: JsonNullable<OrganizationPatchDTO> = JsonNullable.undefined(),
    @JsonProperty("patient")      val patient: JsonNullable<PatientPatchDTO> = JsonNullable.undefined(),
    @JsonProperty("sample")       val sample: JsonNullable<SamplePatchDTO> = JsonNullable.undefined(),

    @JsonProperty("extensions")   val extensions: JsonNullable<List<RequestExtensionRefDTO>> = JsonNullable.undefined()
)

data class OrganizationPatchDTO(
    @field:NotBlank(message = "must not be blank")
    @JsonProperty("serial") val serial: JsonNullable<String> = JsonNullable.undefined()
)
data class PatientPatchDTO(
    @field:NotBlank(message = "must not be blank")
    @JsonProperty("name")  val name:  JsonNullable<String> = JsonNullable.undefined(),
    @JsonProperty("sex")   val sex:   JsonNullable<String> = JsonNullable.undefined(),
    @JsonProperty("age")   val age:   JsonNullable<Int> = JsonNullable.undefined(),
    @JsonProperty("birth") val birth: JsonNullable<String> = JsonNullable.undefined()
)
data class SamplePatchDTO(
    @field:NotBlank(message = "must not be blank")
    @JsonProperty("count")        val count: JsonNullable<Int> = JsonNullable.undefined(),
    @JsonProperty("age")          val age:         JsonNullable<Int>    = JsonNullable.undefined(),
    @field:NotBlank(message = "must not be blank")
    @JsonProperty("sampling_on")  val samplingOn:  JsonNullable<String> = JsonNullable.undefined(),
    @JsonProperty("type")         val type:        JsonNullable<SampleTypePatchDTO> = JsonNullable.undefined()
)
data class SampleTypePatchDTO(
    @field:NotBlank(message = "must not be blank")
    @JsonProperty("serial") val serial: JsonNullable<String> = JsonNullable.undefined()
)

