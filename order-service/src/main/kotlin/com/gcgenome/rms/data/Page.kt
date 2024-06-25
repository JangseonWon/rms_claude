package com.gcgenome.rms.data

data class Page<Any>(
    val totalCount: Int,
    val totalPage: Int,
    val pageSize: Int,
    val currentPage: Int,
    val data: List<Any>
)
