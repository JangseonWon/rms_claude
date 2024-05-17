package com.gcgenome.rms.data

data class Organization(
    var id: String?,
    var user: User?,
    var name: String?,
    var registrationNumber: String?,
    var type: String?,
    var nursingNumber: String?
)