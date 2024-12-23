package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

data class SampleTypeDTO(
    val id: String?,
    val name: String?,
    @JsonProperty("name_kr")
    val nameKr: String?
)