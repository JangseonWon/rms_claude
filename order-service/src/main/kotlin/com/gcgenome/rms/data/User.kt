package com.gcgenome.rms.data

import com.gcgenome.rms.tables.records.UserRecord
import java.util.*

data class User(
    val id: String,
    val name: String,
    val password: String,
    val role: String,
    val type: String,
    val email: String?,
    val phoneNumber: String?,
    val key: UUID?,
    val state: String,
    val branchSerial: String,
    val branchName: String
) {
    companion object{
        fun toModel(record: UserRecord): User{
            return User(
                id = record.id!!,
                name = record.name!!,
                password = record.password!!,
                role = record.role!!,
                type = record.type!!,
                email = record.email,
                phoneNumber = record.phoneNumber,
                key = record.key,
                state = record.state!!,
                branchSerial = record.branchSerial!!,
                branchName = record.branchName!!
            )
        }
    }
}
