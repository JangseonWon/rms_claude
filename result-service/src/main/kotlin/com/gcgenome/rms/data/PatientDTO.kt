package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

data class PatientDTO(
    @JsonProperty("serial")
    var serial: String? = null,
    @JsonProperty("name")
    var name: String? = null,
    @JsonProperty("sex")
    var sex: String? = null,
    @JsonProperty("birth_year")
    var birthYear: Short? = null,
    @JsonProperty("birth_month")
    var birthMonth: Byte? = null,
    @JsonProperty("birth_day")
    var birthDay: Byte? = null,
    @JsonProperty("organization")
    var organization: OrganizationDTO? = null
)
