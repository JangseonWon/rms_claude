package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

data class PatientDTO(
    var serial: String? = null,
    var name: String? = null,
    var sex: String? = null,
    @JsonProperty("birth_year")
    var birthYear: Short? = null,
    @JsonProperty("birth_month")
    var birthMonth: Byte? = null,
    @JsonProperty("birth_day")
    var birthDay: Byte? = null,
    var organization: OrganizationDTO? = null
)
