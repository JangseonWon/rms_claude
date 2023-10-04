package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import java.time.LocalDateTime
import java.util.*

data class Order(
    @JsonProperty("id")
    val id: UUID?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("create_at")
    val createAt: LocalDateTime?,
    @JsonProperty("credit")
    val credit: Boolean?,
    @JsonProperty("last_modify_at")
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    val lastModifyAt: LocalDateTime?,
    @JsonProperty("outsourcing_cost")
    val outsourcingCost:Int?,
    @JsonProperty("price")
    val price: Int?,
    @JsonProperty("test")
    val test: Boolean?,
    @JsonProperty("user_id")
    val userId: String?,

    @JsonProperty("items")
    val items: Array<Item>?
)
