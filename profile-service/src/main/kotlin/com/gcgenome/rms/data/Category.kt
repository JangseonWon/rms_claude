package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class Category(
    var id: UUID? = null,
    var name: String? = null,
    @JsonProperty("order_type")
    var orderType: String? = null
)
