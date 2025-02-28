package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

data class ServiceExtensionDTO(
    @JsonProperty("extension_id")
    var extensionId: String? = null,
    @JsonProperty("service_id")
    var serviceId: String? = null,
    @JsonProperty("required")
    var required: Boolean? = null,
    @JsonProperty("sort_extension")
    var sortExtension: Int? = null
)
