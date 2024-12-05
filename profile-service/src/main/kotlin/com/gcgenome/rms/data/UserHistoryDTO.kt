package com.gcgenome.rms.data

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import java.time.LocalDateTime

data class UserHistoryDTO(
    var id: String? = null,
    var newValue: String? = null,
    var oldValue: String? = null,
    var fieldName: String? = null,
    var userId: String? = null,
    var changedBy: String? = null,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    var changedAt: LocalDateTime? = null
)
