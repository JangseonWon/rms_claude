package com.gcgenome.rms.data

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class Sample_(
    val typeId: String?,
    val organizationId: String?,
    val serial: String?,
    val age: Int?,
    val sampling: LocalDate?,
    val note: String?,
    val department: String?,
    val ward: String?,
    val physician: String?
){
    var id: UUID? = null
    var state: String? = null
    lateinit var extensions: List<Extension_>
}
