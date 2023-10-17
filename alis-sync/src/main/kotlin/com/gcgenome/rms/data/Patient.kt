package com.gcgenome.rms.data

data class Patient(
    val organizationId : String,
    val userId : String,
    val serial:String,
    val sex:String?,
    val name:String?,
    val birthYear: Short?,
    val birthMonth: Byte?,
    val birthDay: Byte?
)
