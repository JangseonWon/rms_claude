package com.gcgenome.rms.data

data class Service_(
    val id: String,
    val name: String,
    var extensions: List<Extension_> = emptyList(),
    var sampleTypes: List<SampleType_> = emptyList()
)