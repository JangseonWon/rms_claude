package com.gcgenome.rms.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "LabSampleCode", schema = "dbo")
data class LabSampleCode(
    @Id
    @Column(name = "SampleID")
    val testCodeId: String,
    @Column(name = "SampleCode")
    val sampleCode: String,
    @Column(name = "SampleFullName")
    val sampleFullName: String
)
