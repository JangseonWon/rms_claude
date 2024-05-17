package com.gcgenome.rms.data

data class Patient(
    var serial: String?,
    var organization: Organization?,
    var name: String?,
    var sex: String?,
    var birthYear: Int?,
    var birthMonth: Int?,
    var birthDay: Int?
)
