package com.gcgenome.rms.data

import java.util.*

data class Item(
    var id: UUID,
    val orderId : UUID,
    val organizationId : String,
    val patientSerial : String,
    val serviceId: String,
    val serial: String?,
    val userId: String
)
