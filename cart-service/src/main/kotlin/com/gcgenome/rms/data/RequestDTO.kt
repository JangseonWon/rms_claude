package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import java.time.LocalDateTime

data class RequestDTO(
    @JsonProperty("service")
    val service: ServiceDTO?,
    @JsonProperty("sample")
    val sample: SampleDTO?,
    @JsonProperty("user_service_id")
    val userServiceId: String?,
    @JsonProperty("status")
    val status: String?,
    @JsonProperty("memo")
    val memo: String?,
    @JsonProperty("department")
    val department: String?,
    @JsonProperty("ward")
    val ward: String?,
    @JsonProperty("physician")
    val physician: String?,
    @JsonProperty("courier_company")
    val courierCompany: String?,
    @JsonProperty("awb_number")
    val awbNumber: String?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("create_at")
    val createAt: LocalDateTime?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("cart_at")
    var cartAt: LocalDateTime?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("specified_at")
    val specifiedAt: LocalDateTime?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("complete_at")
    val completeAt: LocalDateTime?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("resample_at")
    val resampleAt: LocalDateTime?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("reported_at")
    val reportedAt: LocalDateTime?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("last_modify_at")
    val lastModifyAt: LocalDateTime?,
    @JsonProperty("user")
    val user: UserDTO
)

