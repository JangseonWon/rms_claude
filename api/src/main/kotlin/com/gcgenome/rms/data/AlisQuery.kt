package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.gcgenome.rms.config.EmptyStringAsNullDeserializer
import java.time.LocalDate

data class AlisQuery(
    val account: Account,
    val search: Search
){
    companion object{
        data class Account(
            val id: String,
            val pwd: String
        )
        data class Search(
            @JsonDeserialize(using = EmptyStringAsNullDeserializer::class)
            val organization: String,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
            val startdate: LocalDate,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
            val enddate: LocalDate
        )
    }
}
