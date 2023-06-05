package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes


@JsonSubTypes(
    JsonSubTypes.Type(Sample_::class, name = "samples")
)
data class Patient_(
    @JsonProperty("serial")
    val serial:String?,
    @JsonProperty("sex")
    val sex:String?,
    @JsonProperty("name")
    val name:String?,
    @JsonProperty("birth_year")
    val birthYear:Int?,
    @JsonProperty("birth_month")
    val birthMonth:Int?,
    @JsonProperty("birth_day")
    val birthDay:Int?,
    @JsonProperty("samples")
    var samples: List<Sample_>?
) {

}
