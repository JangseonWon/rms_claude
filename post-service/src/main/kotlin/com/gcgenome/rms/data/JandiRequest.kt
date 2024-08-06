package com.gcgenome.rms.data

import com.gcgenome.rms.tables.pojos.Post

data class JandiRequest (
    val userName: String,
    val post: Post,
    val comment: Comment?
)