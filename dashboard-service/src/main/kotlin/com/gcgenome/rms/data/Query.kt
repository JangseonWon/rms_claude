package com.gcgenome.rms.data

data class Query(
    var page: Page,
    var filters: List<Filter>?
){
    data class Page(
        var size: Int,
        var number: Int,
        var totalPage: Int?,
        var totalItem: Int?
    )
    data class Filter(
        var field: String?,
        var value: String?,
        var operator: String?
    )
}
