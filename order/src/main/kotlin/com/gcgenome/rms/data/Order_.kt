package com.gcgenome.rms.data

import java.util.UUID

data class Order_(
    var id: UUID? = null,
    val test: Boolean? = null,
    val credit: Boolean? = null,
    val price: Int? = null,
    val outsourcingCost:Int? = null,
    var item: String? = null
){
    var items: List<Item_> = emptyList()
}
