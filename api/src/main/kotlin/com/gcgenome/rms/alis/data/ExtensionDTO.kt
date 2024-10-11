package com.gcgenome.rms.alis.data

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData

data class ExtensionDTO(
    var extensionId: String? = null,
    @JacksonXmlCData
    var extensionValue: String? = null
)
