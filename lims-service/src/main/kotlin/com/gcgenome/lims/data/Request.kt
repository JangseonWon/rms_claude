package com.gcgenome.lims.data

import java.time.LocalDateTime
import java.util.*

data class Request(
    var sampleId: UUID? = null,
    var serviceId: String? = null,
    var cartAt: LocalDateTime? = null,
    var completeAt: LocalDateTime? = null,
    var createAt: LocalDateTime? = null,
    var credit: Boolean? = null,
    var department: String? = null,
    var empId: String? = null,
    var empMobile: String? = null,
    var empName: String? = null,
    var lastModifyAt: LocalDateTime? = null,
    var memo: String? = null,
    var outsourcingCost: Int? = null,
    var physician: String? = null,
    var price: Int? = null,
    var resampleAt: LocalDateTime? = null,
    var specifiedAt: LocalDateTime? = null,
    var status: String? = null,
    var test: Boolean? = null,
    var userServiceId: String? = null,
    var ward: String? = null
)
