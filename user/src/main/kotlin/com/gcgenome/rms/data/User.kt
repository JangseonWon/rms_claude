package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.gcgenome.rms.tables.records.UserRecord
import com.gcgenome.rms.tables.references.USER
import org.jooq.Record6
import java.util.*

data class User(
    val id: String,
    val authority: String,
    val department: String?,
    val key: UUID?,
    val name: String?,
    val state: String?,
    val code: Short?,
    val password: String?
) {
    companion object {
        fun toModel(user: UserRecord): User =
            User(
                id = user.id!!,
                authority = user.authority!!,
                department = user.department,
                key = user.key,
                name = user.name,
                state = user.state,
                code = user.code,
                password = null
            )
    }
}
