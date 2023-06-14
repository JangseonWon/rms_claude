package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class Order_(
    @JsonProperty("id")
    var id: UUID?,
    @JsonProperty("test")
    val test: Boolean?,
    @JsonProperty("credit")
    val credit: Boolean?,
    @JsonProperty("price")
    val price: Int?,
    @JsonProperty("outsourcing_cost")
    val outsourcingCost:Int?,
    @JsonProperty("items")
    val items: List<Item_>
)
