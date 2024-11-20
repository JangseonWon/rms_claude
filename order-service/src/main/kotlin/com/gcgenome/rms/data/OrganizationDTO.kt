package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

data class OrganizationDTO(
    var id: String? = null,
    @JsonProperty("user_id")
    var userId: String? = null,
    var name: String? = null,
    @JsonProperty("registration_number")
    var registrationNumber: String? = null,
    var type: String? = null,
    @JsonProperty("nursing_number")
    var nursingNumber: String? = null
)
