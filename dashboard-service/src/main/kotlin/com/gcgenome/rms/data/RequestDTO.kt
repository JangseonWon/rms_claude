package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import java.time.LocalDateTime
import java.util.*

data class RequestDTO(
    @JsonProperty("service_id")
    var serviceId: String?,
    @JsonProperty("service")
    var service: ServiceDTO?,
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
    @JsonProperty("courier_company")
    var courierCompany: String? = null,
    @JsonProperty("awb_number")
    var awbNumber: String? = null,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("create_at")
    var createAt: LocalDateTime?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("cart_at")
    var cartAt: LocalDateTime?,
    @JsonProperty("sample")
    var sample: Sample?,
    @JsonProperty("report")
    var report: ReportDTO?,
    @JsonProperty("user")
    var user: UserDTO?,
    @JsonProperty("request_group")
    val requestGroup: RequestGroupDTO?,
    @JsonProperty("request_relation")
    var requestRelation: RequestRelationDTO? = null,
)

