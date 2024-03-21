package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import java.time.LocalDateTime
import java.util.UUID

data class Report(
    @JsonProperty("id")
    val id: UUID?,
    @JsonProperty("create_at")
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    val createAt: LocalDateTime?,
    @JsonProperty("type")
    val type: ReportType,
    @JsonProperty("value")
    val value: String?,
    @JsonProperty("sample_id")
    val sampleId: UUID?,
    @JsonProperty("reported_at")
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    val reportedAt: LocalDateTime?
)
