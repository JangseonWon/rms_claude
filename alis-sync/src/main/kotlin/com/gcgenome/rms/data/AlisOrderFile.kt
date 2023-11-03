package com.gcgenome.rms.data

import java.time.LocalDateTime

data class AlisOrderFile (
    val creatAt: LocalDateTime?,
    val orderNumber: Int,
    val serviceCode: String,
    val filePath: String,
    val fileName: String,
    val fileNameSeq: String,
    val fileSeq: Int,
    val fileExt: String,
    val textFile: String?
)