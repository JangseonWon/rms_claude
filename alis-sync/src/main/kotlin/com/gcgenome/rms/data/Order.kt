package com.gcgenome.rms.data

import java.time.LocalDateTime
import java.util.*

data class Order(
    val id : UUID,
    val createAt: LocalDateTime?,
    val lastModifyAt: LocalDateTime?,
    val test: Boolean?,
    val credit: Boolean?,
    val price: Int?,
    val outsourcingCost: Int?,
    val userId: String
)
