package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import java.util.UUID

@JsonSubTypes(JsonSubTypes.Type(Patient::class, name = "patient"))
data class Item(
    @JsonProperty("id")
    var id: UUID?,
    @JsonProperty("service")
    val service: String?,
    @JsonProperty("serial")
    val serial: String?,
    @JsonProperty("patient")
    val patient: Patient


)
