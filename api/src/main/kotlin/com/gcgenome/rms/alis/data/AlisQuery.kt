package com.gcgenome.rms.alis.data

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
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            val requestDateFrom: LocalDate,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            val requestDateTo: LocalDate,
            @JsonDeserialize(using = EmptyStringAsNullDeserializer::class)
            val userId: String? = null
        )
    }
}
