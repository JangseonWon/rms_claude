package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class ServiceCategory(
    @JsonProperty("service_id")
    val serviceId: String,
    @JsonProperty("service_name")
    val serviceName: String?,
    @JsonProperty("category_id")
    val categoryId: UUID?,
    @JsonProperty("category_name")
    val categoryName: String?
)