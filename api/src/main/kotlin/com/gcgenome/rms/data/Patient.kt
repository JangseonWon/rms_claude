package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

data class Patient(
    @JsonProperty("organization_id")
    val organizationId: String?,
    @JsonProperty("serial")
    val serial: String?,
    @JsonProperty("user_id")
    val userId: String?,
    @JsonProperty("birth_year")
    val birthYear: Short?,
    @JsonProperty("birth_month")
    val birthMonth: Byte?,
    @JsonProperty("birth_day")
    val birthDay: Byte?,
    @JsonProperty("name")
    val name: String?,
    @JsonProperty("sex")
    val sex: String?,
    @JsonProperty("organization")
    val organization: Organization?,
    @JsonProperty("samples")
    val samples: List<Sample>?
) {
    constructor(organizationId: String, serial: String, userId: String) : this(organizationId, serial, userId, null, null, null, null, null, null, null)
}