package com.gcgenome.rms.data

import java.util.*

data class CancelOrder (
    val sampleId: UUID,

    val message: String
){
    var itemId: UUID? = null
}