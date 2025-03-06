package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

data class ServiceExtensionDTO(
    val id: String?,
    val name: String?,
    val regex: String?,
    val required: Boolean?,
    val type: String?,
    @JsonProperty("sort_extension")
    val sortExtension: Int?
)
