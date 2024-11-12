package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import java.time.LocalDateTime

data class PostDTO(
    @JsonProperty("id")
    var id: Long? = null,
    @JsonProperty("title")
    var title: String? = null,
    @JsonProperty("content")
    var content: String? = null,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("create_at")
    var createAt: LocalDateTime? = null,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("last_modify_at")
    var lastModifyAt: LocalDateTime? = null,
    @JsonProperty("read")
    var read: Boolean? = null,
    @JsonProperty("post_category")
    var postCategory: PostCategoryDTO? = null,
    @JsonProperty("user")
    var user: UserDTO? = null,
    @JsonProperty("comments")
    var comments: List<CommentDTO>? = null,
    @JsonProperty("post_files")
    var postFiles: List<PostFileDTO>? = null,
    @JsonProperty("comment_count")
    var commentCount: Int? = null,
    @JsonProperty("read_at")
    var readAt: LocalDateTime? = null,
)