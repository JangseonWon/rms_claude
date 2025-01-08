package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

data class CategoryDTO(
    @JsonProperty("id")
    var id: String?,
    @JsonProperty("name")
    var name: String?,
    @JsonProperty("order_type")
    var orderType: String?,
)
