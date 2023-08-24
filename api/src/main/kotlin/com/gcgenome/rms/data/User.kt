package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.gcgenome.rms.tables.records.UserRecord
import java.util.*

data class User(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("authority")
    val authority: String,
    @JsonProperty("department")
    val department: String?,
    @JsonProperty("key")
    val key: UUID,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("state")
    val state: String,
    @JsonProperty("code")
    val code: Short
){
    companion object {
        fun toModel(user: UserRecord): User =
            User(
                id = user.id!!,
                authority = user.authority!!,
                department = user.department,
                key = user.key!!,
                name = user.name!!,
                state = user.state!!,
                code = user.code!!
            )
    }
}
