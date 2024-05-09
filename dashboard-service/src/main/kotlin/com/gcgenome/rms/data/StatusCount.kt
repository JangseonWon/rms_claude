package com.gcgenome.rms.data

import org.jooq.Record7

data class StatusCount (
    val total: Int?,
    val ordered: Int?,
    val specified: Int?,
    val inProgress: Int?,
    val testFailed: Int?,
    val delivered: Int?,
    val finished: Int?
) {
    companion object{
        fun toModel(record: Record7<Int?, Int?, Int?, Int?, Int?, Int?, Int?>) =
            StatusCount(
                total = record.getValue(0, Int::class.java),
                ordered = record.getValue(1, Int::class.java),
                specified = record.getValue(2, Int::class.java),
                inProgress = record.getValue(3, Int::class.java),
                testFailed = record.getValue(4, Int::class.java),
                delivered = record.getValue(5, Int::class.java),
                finished = record.getValue(6, Int::class.java)
            )
    }
}