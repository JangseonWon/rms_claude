package com.gcgenome.rms.data

import com.gcgenome.lims.tables.records.UserServiceRecord

data class UserService (
    val userId: String?,
    val serviceId: String?
) {
    companion object {
        fun toModel(record: UserServiceRecord) =
            UserService(
                userId = record.getValue("user_id", String::class.java),
                serviceId = record.getValue("service_id", String::class.java)
            )
    }
}