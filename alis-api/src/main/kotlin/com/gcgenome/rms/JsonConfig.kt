package com.gcgenome.rms

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder


@Configuration
open class JsonConfig {
    @Bean
    open fun objectMapper(): ObjectMapper {
        return Jackson2ObjectMapperBuilder.json()
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .serializationInclusion(JsonInclude.Include.NON_EMPTY)
            .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .featuresToDisable(SerializationFeature.FAIL_ON_SELF_REFERENCES)
            .visibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            .modules(JavaTimeModule(), KotlinModule())
            .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .build()
    }
    @Bean
    open fun xmlMapper(): ObjectMapper {
//        val outputFactory = WstxOutputFactory() 필요있는지 모르겠으나, 비어있는 태그에 <tag/> 대신에 <tag></tag>을 적용하는 설정.
//        outputFactory.setProperty(WstxOutputProperties.P_USE_DOUBLE_QUOTES_IN_XML_DECL, java.lang.Boolean.TRUE)
//        outputFactory.setProperty(WstxOutputProperties.P_OUTPUT_EMPTY_ELEMENT_HANDLER,
//            EmptyElementHandler { prefix: String?, localName: String?, nsURI: String?, allowEmpty: Boolean -> false })
//        val xmlMapper = XmlMapper(WstxInputFactory(), outputFactory)
//        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT)
//        xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true)
//        xmlMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
//        xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
//        return xmlMapper
        return XmlMapper()
    }
}