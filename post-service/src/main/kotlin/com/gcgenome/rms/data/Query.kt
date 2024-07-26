package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

data class Query(
    @JsonProperty("sort_by")
    val sortBy: String?,
    val asc: Boolean?,
    val page: Int=1,
    val size: Int=10,
    val filters: MutableList<Filter>?
    ){
    companion object{
        data class Filter(
            val key: String?,
            val operator: String?,
            val value: String?
        )
    }
}
