package com.gcgenome.rms.request.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PastOrPresent
import java.time.LocalDate

data class RequestPostDTO(
    @field:PastOrPresent(message = "must be past or today")
    @JsonProperty("request_data_from")          val requestDateFrom: LocalDate,
    @field:PastOrPresent(message = "must be past or today")
    @JsonProperty("request_data_to")            val requestDateTo: LocalDate,
    @JsonProperty("sample") @field:Valid        val sample: SampleFilter? = null,
    @JsonProperty("service") @field:Valid       val service: ServiceFilter? = null,
    @JsonProperty("organization") @field:Valid  val organization: OrganizationFilter? = null,
    @JsonProperty("patient") @field:Valid       val patient: PatientFilter? = null,
)

data class SampleFilter(
    @field:NotBlank(message = "must not be blank")
    var serial: String
)

data class ServiceFilter(
    @field:NotBlank(message = "must not be blank")
    var serial: String
)

data class OrganizationFilter(
    @field:NotBlank(message = "must not be blank")
    var serial: String
)

data class PatientFilter(
    @field:NotBlank(message = "must not be blank")
    @JsonProperty("serial")
    var serial: String? = null,
    @field:NotBlank(message = "must not be blank")
    @JsonProperty("name")
    var name: String? = null
)
