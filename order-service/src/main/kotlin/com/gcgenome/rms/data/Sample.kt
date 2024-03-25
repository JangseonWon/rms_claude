package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@JsonSubTypes(JsonSubTypes.Type(Extension::class, name = "extensions"))
data class Sample(
    @JsonProperty("id")
    var id: UUID?,
    @JsonProperty("barcode")
    val barcode: String?,
    @JsonProperty("user_sample_id")
    val userSampleId: String?,
    @JsonProperty("quantity")
    val quantity: Int,
    @JsonProperty("age")
    val age: Int?,
    @JsonProperty("sampling_on")
    @JsonDeserialize(using = LocalDateDeserializer::class)
    @JsonSerialize(using = LocalDateSerializer::class)
    val samplingOn: LocalDate,
    @JsonProperty("resample_reason")
    val resampleReason: String?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("create_at")
    val createAt: LocalDateTime?,
    @JsonProperty("sample_type_id")
    val sampleTypeId: String?,
    @JsonProperty("patient_serial")
    val patientSerial: String?,
    @JsonProperty("organization_id")
    val organizationId: String?,
    @JsonProperty("user_id")
    val userId: String?,
    @JsonProperty("extensions")
    val extensions: List<Extension>?,
)
