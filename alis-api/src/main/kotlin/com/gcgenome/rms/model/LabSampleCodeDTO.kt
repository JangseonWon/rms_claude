package com.gcgenome.rms.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

data class LabSampleCodeDTO(
    val sampleCode: String,
    val sampleFullName: String
)
