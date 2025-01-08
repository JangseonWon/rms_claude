package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

data class ServiceDTO (
    @JsonProperty("id")
    var id : String?,
    @JsonProperty("name")
    var name: String?,
    @JsonProperty("name_kr")
    var nameKr: String?,
    @JsonProperty("group_name")
    var groupName: String?,
    @JsonProperty("type")
    var type: String?,
    @JsonProperty("category")
    var category: CategoryDTO?
)