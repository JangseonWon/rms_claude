package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class Order(
    @JsonProperty("id")
    val id: UUID?,
    @JsonProperty("user_id")
    val userId: String?,
    @JsonProperty("items")
    val items: Array<Item>?
)
