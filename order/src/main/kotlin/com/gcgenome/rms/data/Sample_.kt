package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import java.time.LocalDate
import java.util.*

@JsonSubTypes(
    JsonSubTypes.Type(Extension_::class, name = "extensions")
)
data class Sample_(
    @JsonProperty("id")
    var id: UUID? = null,
    @JsonProperty("type_id")
    val typeId: String?,
    @JsonProperty("registration_at")
    var registrationAt: String?,
    @JsonProperty("organization_id")
    val organizationId: String?,
    @JsonProperty("serial")
    val serial: String?,
    @JsonProperty("age")
    val age: Int?,
    @JsonProperty("sampling")
    val sampling: LocalDate?,
    @JsonProperty("note")
    val note: String?,
    @JsonProperty("department")
    val department: String?,
    @JsonProperty("ward")
    val ward: String?,
    @JsonProperty("physician")
    val physician: String?,
    @JsonProperty("state")
    var state: String? = null,
    @JsonProperty("extensions")
    var extensions: List<Extension_>?
)
