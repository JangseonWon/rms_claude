package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import java.time.LocalDateTime

data class RequestDTO(
    @JsonProperty("service")
    var service: ServiceDTO?,
    @JsonProperty("sample")
    var sample: SampleDTO?,
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
    var courierCompany: String?,
    @JsonProperty("awb_number")
    var awbNumber: String?,
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
    @JsonProperty("reported_at")
    var reportedAt: LocalDateTime?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("last_modify_at")
    var lastModifyAt: LocalDateTime?,
    @JsonProperty("user")
    var user: UserDTO? = UserDTO(),
    @JsonProperty("request_group")
    var requestGroup: RequestGroupDTO? = RequestGroupDTO(),
    @JsonProperty("request_relation")
    var requestRelation: RequestRelationDTO? = RequestRelationDTO(id=1)
)

