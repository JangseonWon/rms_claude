package com.gcgenome.rms.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.kotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class XmlConfig {

    @Bean
    fun xmlMapper(): XmlMapper {
        val module = JacksonXmlModule()
        module.setDefaultUseWrapper(false)

        val xmlMapper = XmlMapper(module)
        //xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT)
        xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        xmlMapper.registerModules(kotlinModule(), JavaTimeModule())

        return xmlMapper
    }
}