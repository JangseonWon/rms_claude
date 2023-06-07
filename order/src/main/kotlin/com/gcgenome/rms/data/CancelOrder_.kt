package com.gcgenome.rms.data

import java.util.*

data class CancelOrder_ (
    val sampleId: UUID,

    val message: String
){
    var orderId: UUID? = null
    var itemId: UUID? = null
}