package com.gcgenome.rms.data.patch

import com.fasterxml.jackson.annotation.JsonProperty
import com.gcgenome.rms.data.RequestExtensionDTO
import org.openapitools.jackson.nullable.JsonNullable

data class RequestPatchDTO(
    @JsonProperty("department")   val department:   JsonNullable<String> = JsonNullable.undefined(),
    @JsonProperty("ward")         val ward:         JsonNullable<String> = JsonNullable.undefined(),
    @JsonProperty("physician")    val physician:    JsonNullable<String> = JsonNullable.undefined(),
    @JsonProperty("memo")         val memo:         JsonNullable<String> = JsonNullable.undefined(),
    @JsonProperty("genome_price") val genomePrice:  JsonNullable<Long>   = JsonNullable.undefined(),
    @JsonProperty("labs_price")   val labsPrice:    JsonNullable<Long>   = JsonNullable.undefined(),

    @JsonProperty("organization") val organization: JsonNullable<OrganizationPatchDTO> = JsonNullable.undefined(),
    @JsonProperty("patient")      val patient:      JsonNullable<PatientPatchDTO>      = JsonNullable.undefined(),
    @JsonProperty("sample")       val sample:       JsonNullable<SamplePatchDTO>       = JsonNullable.undefined(),

    @JsonProperty("extensions")   val extensions:   JsonNullable<List<RequestExtensionDTO>> = JsonNullable.undefined()
)
