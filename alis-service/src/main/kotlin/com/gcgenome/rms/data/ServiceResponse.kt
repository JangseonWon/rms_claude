package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

data class ServiceResponse(
    @JsonProperty("TestCode")
    val testCode: String,
    @JsonProperty("TestDisplayName")
    val testDisplayName: String,
    @JsonProperty("SubCodeInfo")
    val subCodeInfo: List<SubCodeInfo>? = null
) {
    companion object {
        data class SubCodeInfo(
            @JsonProperty("TestSubCode")
            val testSubCode: String,
            @JsonProperty("TestSubDisplayName")
            val testSubDisplayName: String
        )
    }
}