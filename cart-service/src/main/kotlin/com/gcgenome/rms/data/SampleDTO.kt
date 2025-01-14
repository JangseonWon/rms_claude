package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class SampleDTO(
    @JsonProperty("id")
    var id: UUID?,
    @JsonProperty("barcode")
    var barcode: String?,
    @JsonProperty("user_sample_id")
    var userSampleId: String?,
    @JsonProperty("quantity")
    var quantity: Int?,
    @JsonProperty("age")
    var age: Int?,
    @JsonProperty("sampling_on")
    @JsonDeserialize(using = LocalDateDeserializer::class)
    @JsonSerialize(using = LocalDateSerializer::class)
    var samplingOn: LocalDate?,
    @JsonProperty("resample_reason")
    var resampleReason: String?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("create_at")
    var createAt: LocalDateTime?,
    @JsonProperty("sample_type")
    var sampleType: SampleTypeDTO?,
    @JsonProperty("patient")
    var patient: PatientDTO?,
    @JsonProperty("extensions")
    var extensions: List<ExtensionDTO>?,
)
