package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class Item(
    @JsonProperty("id")
    var id: UUID?,
    @JsonProperty("serial")
    val serial: String?,
    @JsonProperty("order_id")
    val orderId: UUID?,
    @JsonProperty("organization_id")
    val organizationId: String?,
    @JsonProperty("patient_serial")
    val patientSerial: String?,
    @JsonProperty("user_id")
    val userId: String?,
    @JsonProperty("service")
    val serviceId: String?,

    @JsonProperty("patient")
    val patient: Patient?
)
