package com.gcgenome.rms.data

import java.util.UUID

data class Order_(
    var id: UUID?,
    val test: Boolean?,
    val credit: Boolean?,
    val price: Int?,
    val outsourcingCost:Int?,
    var items: List<Item_>
)
