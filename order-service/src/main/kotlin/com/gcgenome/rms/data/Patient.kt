package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder


@JsonPropertyOrder(value = ["serial", "sex", "name", "birth_year", "birth_month", "birth_day", "organization", "samples"])
data class Patient(
    @JsonProperty("serial")
    val serial: String,
    @JsonProperty("sex")
    val sex: String?,
    @JsonProperty("name")
    val name: String?,
    @JsonProperty("birth_year")
    val birthYear: Short?,
    @JsonProperty("birth_month")
    val birthMonth: Byte?,
    @JsonProperty("birth_day")
    val birthDay: Byte?,
    @JsonProperty("samples")
    val samples: List<Sample>,
    @JsonProperty("organization")
    val organization: Organization
)
