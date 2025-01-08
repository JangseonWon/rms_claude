package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

data class SampleTypeDTO(
    var id: String?,
    var name: String?,
    @JsonProperty("name_kr")
    var nameKr: String?
)
