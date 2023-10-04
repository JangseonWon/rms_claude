package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

data class ServiceSampleType(
    @JsonProperty("sample_type_id")
    val sampleTypeId: String,
    @JsonProperty("service_id")
    val serviceId: String
)
