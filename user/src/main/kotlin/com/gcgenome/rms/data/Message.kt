package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.gcgenome.lims.tables.records.OrganizationRecord
import com.gcgenome.lims.tables.records.OrganizationServiceRecord
import com.gcgenome.lims.tables.records.UserRecord

@JsonPropertyOrder(value = ["id","name","message"])
data class Message (
    @JsonProperty("message")
    val message: String
) {
    @JsonProperty("id")
    var id: String? = null
    @JsonProperty("name")
    var name: String? = null
    @JsonProperty("error")
    var error: String? = null

    companion object {
        fun toModelUser(record: UserRecord, message: String?) =
            Message(
                message = message ?: "GC지놈 담당자에게 문의 바랍니다."
            ).apply {
                id = record.getValue("id", String::class.java)
                name = record.getValue("name", String::class.java)
            }
        fun toModelOrgSer(record: OrganizationServiceRecord, message: String?) =
            Message(
                message = message ?: "GC지놈 담당자에게 문의 바랍니다."
            ).apply {
                id = record.getValue("id", String::class.java)
                name = record.getValue("name", String::class.java)
            }
        fun toModelOrg(record: OrganizationRecord, message: String?) =
            Message(
                message = message ?: "GC지놈 담당자에게 문의 바랍니다."
            ).apply {
                id = record.getValue("id", String::class.java)
                name = record.getValue("name", String::class.java)
            }
    }
}