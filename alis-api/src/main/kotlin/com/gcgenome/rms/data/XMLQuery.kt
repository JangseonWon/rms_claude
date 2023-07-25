package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty


@JsonIgnoreProperties(ignoreUnknown = true)
data class XMLQuery (
    val account : Account? = null,
    val search : Search? = null
) {
    companion object {
        @JsonIgnoreProperties(ignoreUnknown = true)
        data class Account(
            @JsonProperty("id")
            val id: String,
            @JsonProperty("pwd")
            val pwd: String
        )
        @JsonIgnoreProperties(ignoreUnknown = true)
        data class Search(
            @JsonProperty("startdate")
            val startDate : String? = null,
            @JsonProperty("enddate")
            val endDate : String? = null,
            @JsonProperty("brccd")
            val brccd : String? = null,
            @JsonProperty("labpro")
            val labpro : String? = null,
            @JsonProperty("reqno")
            val reqNo : String? = null,
            @JsonProperty("itemcd")
            val itemCd : String? = null,
            @JsonProperty("cstcd")
            val cstCd : String? = null,
            @JsonProperty("canyn")
            val canyn : String? = "A"
        )
    }
}