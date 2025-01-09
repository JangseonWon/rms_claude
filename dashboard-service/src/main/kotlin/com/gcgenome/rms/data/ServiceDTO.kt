package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

data class ServiceDTO (
    @JsonProperty("id")
    val id : String?,
    @JsonProperty("name")
    val name: String?,
    @JsonProperty("category_id")
    val categoryId: String?
)