package com.gcgenome.rms.data

import java.time.LocalDateTime
import java.util.UUID

data class Items_(
    val service: String,
){
    lateinit var patient: Patient_
    var id: UUID? = null
    var orderAt: LocalDateTime? = null
}
