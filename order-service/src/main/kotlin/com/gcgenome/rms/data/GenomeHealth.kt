package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

data class GenomeHealth(
    @JsonProperty("request")
    val request: String?,
    @JsonProperty("fetalSex")
    val fetalSex: Boolean?,
    @JsonProperty("name")
    val name: String?,
    @JsonProperty("mrn")
    val mrn: String?,
    @JsonProperty("birth")
    val birth: String?,
    @JsonProperty("city")
    val city: String?,
    @JsonProperty("state")
    val state: String?,
    @JsonProperty("country")
    val country: String?,
    @JsonProperty("ethnicity")
    val ethnicity: String,
    @JsonProperty("disorder")
    val disorder: String?,
    @JsonProperty("treatment")
    val treatment: String?,
    @JsonProperty("specifications")
    val specifications: String?,
    @JsonProperty("abortion")
    val abortion: Boolean?,
    @JsonProperty("abortionHistory")
    val abortionHistory: Int?,
    @JsonProperty("noOfFetus")
    val noOfFetus: String?,
    @JsonProperty("gestationalAgeWeeks")
    val gestationalAgeWeeks: Int?,
    @JsonProperty("gestationalAgeDays")
    val gestationalAgeDays: Int?,
    @JsonProperty("ivfApplication")
    val ivfApplication: Boolean?,
    @JsonProperty("patientHeight")
    val patientHeight: Int?,
    @JsonProperty("patientWeight")
    val patientWeight: Int?,
    @JsonProperty("bmi")
    val bmi: Int?,
    @JsonProperty("screeningTest")
    val screeningTest: Boolean?,
    @JsonProperty("screeningTestRisk")
    val screeningTestRisk: String?,
    @JsonProperty("significantFeatures")
    val significantFeatures: String?,
    @JsonProperty("nt")
    val nt: Int?,
    @JsonProperty("dateOfCollection")
    val dateOfCollection: String?
)