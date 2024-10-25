package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

data class Query(
    @JsonProperty("sort_by")
    var sortBy: String? = null,
    var asc: Boolean? = null,
    var page: Int? = null,
    var size: Int? = null,
    @JsonProperty("filter_groups")
    var filterGroups: MutableList<FilterGroup>? = null
){
    data class FilterGroup(
        @JsonProperty("condition_type")
        var conditionType: String = "AND",
        var filters: List<Filter>
    ){
        data class Filter(
            var table: String,
            var column: String,
            var operator: String,
            var value: String
        )
    }
}