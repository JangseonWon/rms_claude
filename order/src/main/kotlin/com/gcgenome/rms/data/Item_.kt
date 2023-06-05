package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.time.LocalDateTime
import java.util.UUID

@JsonSubTypes(
    JsonSubTypes.Type(Patient_::class, name = "patient")
)
data class Item_(
    @JsonProperty("service")
    val service: String,
    @JsonProperty("order_at")
    var orderAt: String?,
    @JsonProperty("patient")
    var patient: Patient_
){
    @JsonProperty("id")
    var id: UUID? = null
}
