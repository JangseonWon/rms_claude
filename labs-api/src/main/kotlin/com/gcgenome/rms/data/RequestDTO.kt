package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.gcgenome.rms.tables.RequestExtension
import java.time.LocalDateTime
import java.util.*

data class RequestDTO(
    @JsonProperty("id")
    var id: UUID? = null,
    @JsonProperty("department")
    var department: String? = null,
    @JsonProperty("ward")
    var ward: String? = null,
    @JsonProperty("physician")
    var physician: String? = null,
    @JsonProperty("memo")
    var memo: String? = null,
    @JsonProperty("genome_price")
    var genomePrice: Long? = null,
    @JsonProperty("labs_price")
    var labsPrice: Long? = null,
    @JsonProperty("create_at")
    var createAt: LocalDateTime? = null,
    @JsonIgnore
    var isDeletable: Boolean? = null,
    @JsonIgnore
    var isEditable: Boolean? = null,

    @JsonIgnore
    var serviceId: UUID? = null,
    @JsonIgnore
    var sampleId: UUID? = null,
    @JsonIgnore
    var organizationId: UUID? = null,
    @JsonIgnore
    var patientId: UUID? = null,

    @JsonProperty("organization")
    var organization: OrganizationDTO? = null,
    @JsonProperty("patient")
    var patient: PatientDTO? = null,
    @JsonProperty("service")
    var service: ServiceDTO? = null,
    @JsonProperty("sample")
    var sample: SampleDTO? = null,
    @JsonProperty("extensions")
    var extensions: List<RequestExtensionDTO>? = null

)
