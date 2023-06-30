package com.gcgenome.rms.data

data class Service(
    val id: String,
    val name: String,
    var extensions: List<Extension> = emptyList(),
    var sampleTypes: List<SampleType> = emptyList()
)