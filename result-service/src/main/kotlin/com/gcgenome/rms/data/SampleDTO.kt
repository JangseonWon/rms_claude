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
import java.util.UUID

data class SampleDTO(
    @JsonProperty("id")
    var id: UUID? = null,
    @JsonProperty("barcode")
    var barcode: String? = null,
    @JsonProperty("user_sample_id")
    var userSampleId: String? = null,
    @JsonProperty("quantity")
    var quantity: Int? = null,
    @JsonProperty("age")
    var age: Int? = null,
    @JsonDeserialize(using = LocalDateDeserializer::class)
    @JsonSerialize(using = LocalDateSerializer::class)
    @JsonProperty("sampling_on")
    var samplingOn: LocalDate? = null,
    @JsonProperty("resample_reason")
    var resampleReason: String? = null,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("create_at")
    var createAt: LocalDateTime? = null,
    @JsonProperty("patient")
    var patient: PatientDTO? = null,
    @JsonProperty("sample_type")
    var sampleType: SampleTypeDTO? = null,
    @JsonProperty("extensions")
    var extensions: List<ExtensionDTO>? = null
)
