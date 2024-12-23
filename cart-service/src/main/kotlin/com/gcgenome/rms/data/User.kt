package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

data class User(
    var id: String?,
    var name: String?,
    @JsonProperty("branch_serial")
    var branchSerial: String?,
    @JsonProperty("branch_name")
    var branchName: String?
)
