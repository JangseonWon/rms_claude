package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class RequestSearchDTO(
    @JsonProperty("request_data_from")
    var requestDateFrom: LocalDate? = null,

    @JsonProperty("request_data_to")
    var requestDateTo: LocalDate? = null,

    @JsonProperty("sample")
    var sample: SampleFilter? = null,

    @JsonProperty("service")
    var service: ServiceFilter? = null,

    @JsonProperty("organization")
    var organization: OrganizationFilter? = null,

    @JsonProperty("patient")
    var patient: PatientFilter? = null,
)

data class SampleFilter(
    @JsonProperty("serial")
    var serial: String? = null
)

data class ServiceFilter(
    @JsonProperty("serial")
    var serial: String? = null
)

data class OrganizationFilter(
    @JsonProperty("serial")
    var serial: String? = null
)

data class PatientFilter(
    @JsonProperty("serial")
    var serial: String? = null,
    @JsonProperty("name")
    var name: String? = null
)