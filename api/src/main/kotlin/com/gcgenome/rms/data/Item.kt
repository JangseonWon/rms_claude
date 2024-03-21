package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class Item(
    @JsonProperty("order_id")
    val orderId: UUID?,
    @JsonProperty("service")
    val serviceId: String?,
    @JsonProperty("serial")
    val serial: String?,
    @JsonProperty("samples")
    val samples: Array<Sample>?
)
