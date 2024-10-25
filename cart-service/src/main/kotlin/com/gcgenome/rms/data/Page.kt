package com.gcgenome.rms.data

data class Page<T>(
    val totalCount: Int? = null,
    val totalPage: Int? = null,
    val pageSize: Int? = null,
    val currentPage: Int? = null,
    val data: List<T>
)
