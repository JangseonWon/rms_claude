package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.gcgenome.lims.tables.records.OrganizationRecord

data class Organization(
    @JsonProperty("id")
    val id:String?,
    @JsonProperty("name")
    val name:String?,
    @JsonProperty("type")
    val type:String?,
    @JsonProperty("registration_number")
    val registrationNumber:String?,
    @JsonProperty("nursing_number")
    val nursingNumber:String?,
    @JsonProperty("branch_code")
    val branchCode:String?,
    @JsonProperty("branch_name")
    val branchName:String?
) {
    companion object {
        fun toModel(record: OrganizationRecord) =
            Organization(
                id = record.getValue("id", String::class.java),
                name = record.getValue("name", String::class.java),
                type = record.getValue("type", String::class.java),
                registrationNumber = record.getValue("registration_number", String::class.java),
                nursingNumber = record.getValue("nursing_number", String::class.java),
                branchCode = record.getValue("branch_id", String::class.java),
                branchName = record.getValue("branch_name", String::class.java)
            )
    }

}