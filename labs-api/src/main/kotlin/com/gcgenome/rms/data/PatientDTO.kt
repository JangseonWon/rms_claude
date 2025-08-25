package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class PatientDTO(
    @JsonIgnore
    var id: UUID? = null,
    @JsonProperty("serial")
    var serial: String? = null,
    @JsonProperty("name")
    var name: String? = null,
    @JsonProperty("sex")
    var sex: String? = null,
    @JsonProperty("birth")
    var birth: LocalDate? = null
)
