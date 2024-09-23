package com.gcgenome.rms.data


data class ExtensionDTO(
    var id: String? = null,
    var name: String? = null,
    var required: Boolean? = null,
    var regex: String? = null,
    var type: String? = null
)
