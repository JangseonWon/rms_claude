package com.gcgenome.rms.alis.data

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData

data class OrganizationDTO(
    var organizationId: String? = null,
    @JacksonXmlCData
    var organizationName: String? = null,
    var registrationNumber: String? = null,
    var nursingNumber: String? = null
)
