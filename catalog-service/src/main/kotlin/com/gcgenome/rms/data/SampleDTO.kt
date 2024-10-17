package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.gcgenome.rms.tables.pojos.SampleType
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@JsonSubTypes(JsonSubTypes.Type(ExtensionDTO::class, name = "extensions"))
data class SampleDTO(
    @JsonProperty("id")
    var id: UUID?,
    @JsonProperty("barcode")
    val barcode: String?,
    @JsonProperty("user_sample_id")
    val userSampleId: String?,
    @JsonProperty("quantity")
    val quantity: Int?,
    @JsonProperty("age")
    val age: Int?,
    @JsonProperty("sampling_on")
    @JsonDeserialize(using = LocalDateDeserializer::class)
    @JsonSerialize(using = LocalDateSerializer::class)
    val samplingOn: LocalDate?,
    @JsonProperty("resample_reason")
    val resampleReason: String?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("create_at")
    val createAt: LocalDateTime?,
    @JsonProperty("sample_type")
    val sampleType: SampleType?,
    @JsonProperty("patient")
    val patient: PatientDTO?,
    @JsonProperty("extensions")
    val extensions: List<ExtensionDTO>?,
)
