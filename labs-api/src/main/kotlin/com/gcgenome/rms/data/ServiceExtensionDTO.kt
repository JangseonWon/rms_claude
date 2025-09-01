package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class ServiceExtensionDTO(
    @JsonProperty("id")
    var id: UUID? = null,
    @JsonProperty("is_required")
    var isRequired: Boolean? = null,
    var serviceId: UUID? = null,
    var extensionId: UUID? = null,
    var extension: ExtensionDTO? = null

)
