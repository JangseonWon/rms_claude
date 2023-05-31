package com.gcgenome.rms.data

import java.util.UUID

data class Order_(
    val test: Boolean? = null,
    val credit: Boolean? = null,
    val price: Int? = null,
    val outsourcingCost:Int? = null
){
    var id: UUID? = null
    lateinit var items: List<Items_>
}
