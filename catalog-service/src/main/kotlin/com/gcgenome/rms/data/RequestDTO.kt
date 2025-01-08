package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.gcgenome.rms.tables.pojos.Service
import java.time.LocalDateTime
import java.util.*

@JsonSubTypes(JsonSubTypes.Type(PatientDTO::class, name = "patient"))
data class RequestDTO(
    @JsonProperty("service_id")
    var serviceId: String?,
    @JsonProperty("service")
    var service: Service?,
    @JsonProperty("sample_id")
    var sampleId: UUID?,
    @JsonProperty("serial")
    var serial: String?,
    @JsonProperty("user_service_id")
    var userServiceId: String?,
    @JsonProperty("status")
    var status: String?,
    @JsonProperty("memo")
    var memo: String?,
    @JsonProperty("department")
    var department: String?,
    @JsonProperty("ward")
    var ward: String?,
    @JsonProperty("physician")
    var physician: String?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("create_at")
    var createAt: LocalDateTime?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("cart_at")
    var cartAt: LocalDateTime?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("specified_at")
    var specifiedAt: LocalDateTime?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("complete_at")
    var completeAt: LocalDateTime?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("resample_at")
    var resampleAt: LocalDateTime?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("last_modify_at")
    var lastModifyAt: LocalDateTime?,
    @JsonProperty("sample")
    var sample: SampleDTO?,
    @JsonProperty("user_id")
    var userId: String?
)

