package com.gcgenome.rms.data.patch

import com.fasterxml.jackson.annotation.JsonProperty
import org.openapitools.jackson.nullable.JsonNullable

data class SamplePatchDTO(
    @JsonProperty("count")        val count: JsonNullable<Int> = JsonNullable.undefined(),
    @JsonProperty("age")          val age:         JsonNullable<Int>    = JsonNullable.undefined(),
    @JsonProperty("sampling_on")  val samplingOn:  JsonNullable<String> = JsonNullable.undefined(),
    @JsonProperty("type")         val type:        JsonNullable<SampleTypePatchDTO> = JsonNullable.undefined()
)
