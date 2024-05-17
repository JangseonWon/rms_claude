package com.gcgenome.rms.data

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import java.time.LocalDateTime
import java.util.*

data class Request(
    var orderId: UUID?,
    var serviceId: String?,
    var sampleId: String?,
    var userServiceId: String?,
    var status: String?,
    var memo: String?,
    var department: String?,
    var ward: String?,
    var physician: String?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    var createAt: LocalDateTime?,
    var sample: Sample?,
    var service: Service?
)