package com.gcgenome.rms.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "LabTestCode", schema = "dbo")
data class LabTestCode(
    @Id
    @Column(name = "TestCodeID")
    val testCodeId: String,
    @Column(name = "TestCode")
    val testCode: String,
    @Column(name = "TestDisplayName")
    val testDisplayName: String,
    @Column(name = "IsTestSub")
    val isTestSub: Boolean,
    @Column(name = "IsTestUse")
    val isTestUse: Boolean
)
