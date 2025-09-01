package com.gcgenome.rms.entity

import java.time.LocalDate
import java.util.*

data class PatientEntity(
    val id: UUID,
    val serial: String? = null,
    val name: String,
    val sex: String? = null,
    val age: Int? = null,
    val birth: LocalDate? = null,
)
