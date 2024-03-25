package com.gcgenome.rms.data

import reactor.core.publisher.Flux

data class Page(
    val totalCount: Int,
    val totalPage: Int,
    val pageSize: Int,
    val currentPage: Int,
    val data: Flux<Any>
)
