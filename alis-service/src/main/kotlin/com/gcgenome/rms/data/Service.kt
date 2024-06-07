package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class Service(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("category_id")
    val categoryId: UUID?
)