package com.gcgenome.rms.data

data class Patient_(
    val serial:String,
    val sex:String,
    val name:String,
    val birthYear:Int?,
    val birthMonth:Int?,
    val birthDay:Int?,
) {
    lateinit var samples: List<Sample_>
}
