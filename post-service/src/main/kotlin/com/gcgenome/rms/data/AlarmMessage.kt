package com.gcgenome.rms.data

data class AlarmMessage(
    val userId: String,
    val alarmCount: Int,
    val post: PostDTO
)
