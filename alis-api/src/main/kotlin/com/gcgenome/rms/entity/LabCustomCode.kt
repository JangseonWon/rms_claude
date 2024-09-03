package com.gcgenome.rms.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "LabCustomCode", schema = "dbo")
data class LabCustomCode(
    @Id
    @Column(name = "CustomCodeID")
    val customCodeID: String,
    @Column(name = "CustomCode")
    val customCode: String,
    @Column(name = "CustomDisplayName")
    val customDisplayName: String
)
