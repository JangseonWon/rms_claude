package com.gcgenome.rms.data

data class Extension(
    val id: String,
    val name: String? = null,
    val required: Boolean? = null,
    val regex: String? = null,
)
