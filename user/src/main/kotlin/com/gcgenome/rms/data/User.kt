package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.gcgenome.rms.tables.records.UserRecord
import java.util.*

data class User(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("authority")
    val authority: String?,
    @JsonProperty("department")
    val department: String?,
    @JsonProperty("key")
    val key: UUID?,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("state")
    val state: String?
) {
    @JsonProperty("password")
    val password: String? = null

    companion object {
        fun toModel (record: UserRecord) =
            User(
                id = record.getValue("id", String::class.java),
                authority = record.getValue("authority", String::class.java),
                department = record.getValue("department", String::class.java),
                key = record.getValue("key", UUID::class.java),
                name = record.getValue("name", String::class.java),
                state = record.getValue("state", String::class.java),
            )

    }
}
