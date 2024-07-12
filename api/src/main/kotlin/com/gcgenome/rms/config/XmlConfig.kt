package com.gcgenome.rms.config

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.kotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Configuration
class XmlConfig {

    @Bean
    fun xmlMapper(): XmlMapper {
        val module = JacksonXmlModule()
        module.setDefaultUseWrapper(false)

        val xmlMapper = XmlMapper(module)
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT)
        xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        xmlMapper.registerModules(kotlinModule(), JavaTimeModule())

        return xmlMapper
    }
}