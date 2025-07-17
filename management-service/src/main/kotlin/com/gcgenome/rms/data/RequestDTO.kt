package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import java.time.LocalDateTime

data class RequestDTO(
    @JsonProperty("user_service_id")
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
    var service: ServiceDTO? = null,
    var sample: SampleDTO? = null,
    @JsonProperty("request_group")
    var requestGroup: RequestGroupDTO? = null,
    @JsonProperty("user")
    var user: UserDTO? = null,
    var isCancel: Boolean? = null,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("cancel_at")
    var cancelAt: LocalDateTime? = null,
    @JsonProperty("lims_resample_reason")
    var limsResampleReason: String? = null,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("lims_resample_at")
    var limsResampleAt: LocalDateTime? = null,
)