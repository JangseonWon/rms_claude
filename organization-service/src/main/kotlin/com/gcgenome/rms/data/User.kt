package com.gcgenome.rms.data

import com.gcgenome.rms.tables.records.UserRecord
import java.util.*

data class User(
    val id: String,
    //val authority: String,
    //val department: String?,
    val key: UUID?,
    val name: String,
    val state: String
) {
    companion object{
        fun toModel(record: UserRecord): User{
            return User(
                id = record.id!!,
                //authority = record.authority!!,
                //department = record.department,
                key = record.key!!,
                name = record.name!!,
                state = record.state!!
            )
        }
    }
}