package com.gcgenome.rms.data

data class Organization (
    val id:String,
    val name:String?,
    val type:String?,
    val userId: String,
    val registrationNumber:String?,
    val nursingNumber:String?,
    val branchId:String?,
    val branchName:String?
)