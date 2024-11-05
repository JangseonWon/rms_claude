package com.gcgenome.rms.data

import org.jooq.Record7

data class StatusCount (
    val total: Int?,
    val unconfirmedOrder: Int?,
    val completedOrder: Int?,
    val inProgress: Int?,
    val testFailed: Int?,
    val delivered: Int?,
    val completed: Int?
) {
    companion object{
        fun toModel(record: Record7<Int?, Int?, Int?, Int?, Int?, Int?, Int?>) =
            StatusCount(
                total = record.getValue(0, Int::class.java),
                unconfirmedOrder = record.getValue(1, Int::class.java),
                completedOrder = record.getValue(2, Int::class.java),
                inProgress = record.getValue(3, Int::class.java),
                testFailed = record.getValue(4, Int::class.java),
                delivered = record.getValue(5, Int::class.java),
                completed = record.getValue(6, Int::class.java)
            )
    }
}