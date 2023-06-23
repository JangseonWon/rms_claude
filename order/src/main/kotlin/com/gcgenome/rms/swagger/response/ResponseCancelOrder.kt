package com.gcgenome.rms.swagger.response

import java.util.*

data class ResponseCancelOrder (
    val sampleId: UUID,
    val message: String,
    val orderId: UUID,
    val itemId: UUID
)