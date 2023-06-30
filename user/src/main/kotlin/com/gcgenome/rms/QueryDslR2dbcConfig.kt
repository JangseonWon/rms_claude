package com.gcgenome.rms

import com.querydsl.sql.PostgreSQLTemplates
import com.querydsl.sql.SQLTemplates
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class QueryDslR2dbcConfig {
    @Bean
    fun sqlTemplates(): SQLTemplates {
        return PostgreSQLTemplates.builder().printSchema().newLineToSingleSpace().build()
    }
}