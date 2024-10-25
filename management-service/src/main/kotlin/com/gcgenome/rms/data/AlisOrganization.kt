package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

class AlisOrganization (
    @JsonProperty("comp_code")
    val compCode: String,
    @JsonProperty("comp_name")
    val compName: String,
    @JsonProperty("comp_mng_name")
    val compMngName: String,
    @JsonProperty("comp_mng_begin_no")
    val compMngBeginNo: String

)