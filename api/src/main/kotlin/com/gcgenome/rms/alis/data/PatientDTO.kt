package com.gcgenome.rms.alis.data

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData

data class PatientDTO(
    @JacksonXmlCData
    var serial: String? = null,
    @JacksonXmlCData
    var patientName: String? = null,
    var sex: String? = null,
    var birth: Birth? = null

){
    companion object{
        data class Birth (
            var year: Short? = null,
            var month: Byte? = null,
            var day: Byte? = null
        )

    }
}
