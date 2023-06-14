package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import java.time.LocalDateTime
import java.util.UUID

@JsonSubTypes(JsonSubTypes.Type(Patient_::class, name = "patient"))
data class Item_(
    @JsonProperty("id")
    var id: UUID?,
    @JsonProperty("service")
    val service: String,
    @JsonProperty("order_at")
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    val orderAt: LocalDateTime?,
    @JsonProperty("patient")
    val patient: Patient_
)
