package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

data class Query(
    @JsonProperty("page")
    val page: Int,
    @JsonProperty("size")
    val size: Int,
    @JsonProperty("sort_by")
    val sortBy: String?,
    @JsonProperty("asc")
    var asc: Boolean = true,
    @JsonProperty("filters")
    val filters: MutableList<Filter>?
){
    companion object {
        data class Filter(
            val key: String?,
            val value: String?,
            val operator: String?
        )
    }
}
