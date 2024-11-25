package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

data class SampleTypeDTO(
    var id: String? = null,
    var name: String? = null,
    @JsonProperty("name_kr")
    var nameKr: String? = null,
)