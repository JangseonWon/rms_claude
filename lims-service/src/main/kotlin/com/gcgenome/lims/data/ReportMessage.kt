package com.gcgenome.lims.data

import java.time.LocalDate

data class ReportMessage(
    var institutionName: String?,
    var departmentName: String?,
    var wardName: String?,
    var sex: String?,
    var birth: LocalDate?,
    var mrn: String?,
    var info: String?,
    var institution: String?,
    var physician: String?,
    var sample: Long?,
    var service: String?,
    var report: ByteArray?
)
