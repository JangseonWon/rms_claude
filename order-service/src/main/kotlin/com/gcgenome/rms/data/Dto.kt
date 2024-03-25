package com.gcgenome.rms.data

import java.util.*

data class Dto (
    val orderId: UUID?,
    val sampleId: UUID?,
    val userId: String?,
    val serviceId: String
)