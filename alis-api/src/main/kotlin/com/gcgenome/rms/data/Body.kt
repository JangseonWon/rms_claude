package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Body (
    @field:JacksonXmlElementWrapper(localName = "requests")
    @field:JacksonXmlProperty(localName = "request")
    val requests: List<Request>? = null,
    val error : Error? = null
){
    companion object{
        data class Error(
            val message : String
        )
    }
}