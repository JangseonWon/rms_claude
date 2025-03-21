package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

data class RequestRelationDTO(
    @JsonProperty("id")
    val id: Int?,
    @JsonProperty("name")
    val name: String?
)