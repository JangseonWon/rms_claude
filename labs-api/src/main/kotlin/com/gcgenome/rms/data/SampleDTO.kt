package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class SampleDTO(
    @JsonIgnore
    var id: UUID? = null,
    @JsonProperty("barcode")
    var barcode: String? = null,
    @JsonProperty("serial")
    var serial: String? = null,
    @JsonProperty("count")
    var count: Int? = null,
    @JsonProperty("sampling_on")
    var samplingOn: LocalDate? = null,
    @JsonIgnore
    var createAt: LocalDateTime? = null,

    @JsonIgnore
    var sampleTypeId: UUID? = null,
    @JsonIgnore
    var userId: UUID? = null,

    @JsonProperty("type")
    var type: SampleTypeDTO? = null
)
