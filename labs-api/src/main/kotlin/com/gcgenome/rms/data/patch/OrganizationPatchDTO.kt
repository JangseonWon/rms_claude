package com.gcgenome.rms.data.patch

import com.fasterxml.jackson.annotation.JsonProperty
import org.openapitools.jackson.nullable.JsonNullable

data class OrganizationPatchDTO(
    @JsonProperty("serial") val serial: JsonNullable<String> = JsonNullable.undefined()
)

