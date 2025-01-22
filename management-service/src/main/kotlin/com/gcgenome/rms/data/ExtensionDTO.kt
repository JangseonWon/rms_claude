package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty


data class ExtensionDTO(
    var id: String? = null,
    var name: String? = null,
    var value: String? = null,
    @JsonProperty("name_kr")
    var nameKr: String? = null,
    var required: Boolean? = null,
    var regex: String? = null,
    var type: String? = null
)
