package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(schema = "rms_dev", name = "sample", indexes=[
    Index(unique = true, columnList = "barcode"),
    Index(columnList = "user_id, user_sample_id")
])
data class Sample(
    @Id @Column(name = "id")
    val id: UUID,
    @Column(name = "barcode", length = 64, nullable = true, unique = true)
    val barcode: String,
    @Column(name = "user_sample_id", nullable = true)
    val userSampleId: String,
    @Column(name = "quantity", nullable = false)
    val quantity: Int,
    @Column(name = "age", nullable = true)
    val age: Int,
    @Column(name = "sampling_on", nullable = false)
    val samplingOn: LocalDate,
    @Column(name = "resample_reason", nullable = true)
    val resampleReason: String,
    @Column(name = "create_at", nullable = true)
    val createAt: LocalDateTime,


    @ManyToOne
    @JoinColumn(name = "sample_type_id", insertable = false, updatable = false, nullable = false)
    val sampleTypeId: SampleType,
    @ManyToOne
    @JoinColumns(value = [
        JoinColumn(name = "patient_serial", referencedColumnName = "serial", insertable=false, updatable=false, nullable = false),
        JoinColumn(name = "organization_id", referencedColumnName = "organization_id", insertable=false, updatable=false, nullable = false),
        JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable=false, updatable=false, nullable = false)
    ])
    val patientId: Patient,


    @OneToMany(mappedBy = "sampleId")
    val sampleExtension: List<SampleExtension>,
    @OneToMany(mappedBy = "sampleId")
    val request: List<Request>
)
