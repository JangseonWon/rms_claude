package com.gcgenome.rms.alis.data

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

data class Body(
    @JacksonXmlElementWrapper(localName = "requests")
    @JacksonXmlProperty(localName = "request")
    var requests: List<RequestDTO>?
)