package com.gcgenome.rms.data

data class Patient(
    var serial: String?,
    var organization: Organization?,
    var name: String?,
    var sex: String?,
    var birthYear: Short?,
    var birthMonth: Byte?,
    var birthDay: Byte?
)
