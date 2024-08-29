package com.gcgenome.rms.data

data class ServiceExtension(
    val id: String,
    val name: String,
    val regex: String,
    val required: Boolean,
    val type: String?
)
