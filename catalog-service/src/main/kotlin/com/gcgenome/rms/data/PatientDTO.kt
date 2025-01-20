package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

data class PatientDTO(
    @JsonProperty("serial")
    val serial: String?,
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
    @JsonProperty("organization")
    val organization: OrganizationDTO?
)
