package com.gcgenome.rms.config

import LoggingWebFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WebFilterConfig {
    @Bean
    fun loggingWebFilter(): LoggingWebFilter {
        return LoggingWebFilter()
    }
}