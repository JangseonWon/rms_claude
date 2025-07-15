package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import java.time.LocalDateTime

data class RequestDTO(
    var userServiceId: String? = null,
    var status: Status? = null,
    var memo: String? = null,
    var department: String? = null,
    var ward: String? = null,
    var physician: String? = null,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("create_at")
    var createAt: LocalDateTime? = null,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("cart_at")
    var cartAt: LocalDateTime? = null,
    @JsonProperty("courier_company")
    var courierCompany: String? = null,
    @JsonProperty("awb_number")
    var awbNumber: String? = null,
    @JsonProperty("request_group")
    val requestGroup: RequestGroupDTO?,
    @JsonProperty("request_relation")
    var requestRelation: RequestRelationDTO? = null,
    var service: ServiceDTO? = null,
    var sample: SampleDTO? = null,
    var user: UserDTO? = null,
    var isCancel: Boolean? = null
)
