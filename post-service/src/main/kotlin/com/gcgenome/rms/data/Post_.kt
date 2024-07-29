package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.gcgenome.rms.tables.pojos.User
import java.time.LocalDateTime
import java.util.*

data class Post_(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("title")
    val title: String,
    @JsonProperty("content")
    val content: String?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("create_at")
    val createAt: LocalDateTime,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("last_modify_at")
    val lastModifyAt: LocalDateTime,
    @JsonProperty("read")
    val read: Boolean,
    @JsonProperty("user_id")
    val userId: String?,
    @JsonProperty("post_category_id")
    val postCategoryId: UUID,
    @JsonProperty("user")
    val user: User?,
    @JsonProperty("files")
    val files: Array<File_>?,
    @JsonProperty("comments")
    val comments: Array<Comment>?,
)