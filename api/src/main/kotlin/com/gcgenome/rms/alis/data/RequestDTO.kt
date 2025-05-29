package com.gcgenome.rms.alis.data

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import java.time.LocalDateTime

data class RequestDTO(
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    var requestAt: LocalDateTime? = null,
    var requestId: String? = null,
    var serviceId: String? = null,
    @JacksonXmlCData
    var department: String? = null,
    @JacksonXmlCData
    var ward: String? = null,
    @JacksonXmlCData
    var physician: String? = null,
    @JacksonXmlCData
    var memo: String? = null,
    var user: UserDTO? = null,
    var sample: SampleDTO? = null,
)
