package com.gcgenome.rms.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "ProgCompCode", schema = "dbo")
data class ProgCompCode(
    @Id
    @Column(name = "CompCodeID")
    val compCodeId: String,
    @Column(name = "CompCode")
    val compCode: String,
    @Column(name = "CompName")
    val compName: String,
    @Column(name = "CompMngCode")
    val compMngCode: String
)
