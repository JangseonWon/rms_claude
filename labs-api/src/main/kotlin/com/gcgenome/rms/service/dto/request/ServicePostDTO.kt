package com.gcgenome.rms.service.dto.request

import com.fasterxml.jackson.annotation.JsonProperty

data class ServicePostDTO(
    @JsonProperty("code")        val code: String? = null,
    @JsonProperty("serial")      val serial: String? = null,
    @JsonProperty("name_kr")     val nameKr: String? = null,
    @JsonProperty("name_en")     val nameEn: String? = null,
)
