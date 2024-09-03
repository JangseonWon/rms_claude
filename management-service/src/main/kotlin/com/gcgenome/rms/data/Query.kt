package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

data class Query(
    @JsonProperty("sort_by")
    var sortBy: String? = null,
    var asc: Boolean? = null,
    var page: Int,
    var size: Int,
    var filters: MutableList<Filter>? = null
    ){
    companion object{
        data class Filter(
            val key: String?,
            val operator: String?,
            val value: String?
        )
    }
}
