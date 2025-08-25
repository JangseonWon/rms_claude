package com.gcgenome.rms.data.patch

import com.fasterxml.jackson.annotation.JsonProperty
import org.openapitools.jackson.nullable.JsonNullable

data class PatientPatchDTO(
    @JsonProperty("name")  val name:  JsonNullable<String> = JsonNullable.undefined(),
    @JsonProperty("sex")   val sex:   JsonNullable<String> = JsonNullable.undefined(),
    @JsonProperty("birth") val birth: JsonNullable<String> = JsonNullable.undefined()   // yyyy-MM-dd
)