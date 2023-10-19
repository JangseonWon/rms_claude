package com.gcgenome.rms.data

import java.util.*

data class User(
    val id: String,
    val authority: String,
    val department: String?,
    val key: UUID?,
    val name: String?,
    val state: String?,
    val code: Short?
)

