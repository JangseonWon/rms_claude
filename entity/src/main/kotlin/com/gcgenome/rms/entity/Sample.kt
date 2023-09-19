package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(schema = "public", name = "sample")
data class Sample(
    @Id @Column(name = "id")
    val id: UUID,
    @Column(name = "create_at", nullable = false)
    val createAt: LocalDateTime,
    @Column(name = "last_modify_at", nullable = false)
    val lastModifyAt: LocalDateTime,
    @Column(name = "genome_barcode_prefix")
    val genomeBarcodePrefix: Int,
    @Column(name = "genome_barcode_infix")
    val genomeBarcodeInfix: Short,
    @Column(name = "genome_barcode_postfix")
    val genomeBarcodePostfix: Int,
    @Column(name = "genome_barcode", length = 64)
    val genomeBarcode: String,
    @Column(name = "sample_barcode", length = 64)
    val sampleBarcode: String,
    @Column(name = "age")
    val age: Int,
    @Column(name = "sampling", nullable = false)
    val sampling: LocalDateTime,
    @Column(name = "note", length = 64)
    val note: String,
    @Column(name = "state", length = 64)
    val state: String,
    @Column(name = "registration_at")
    val registrationAt: LocalDateTime,
    @Column(name = "department", length = 64)
    val department: String,
    @Column(name = "ward", length = 64)
    val ward: String,
    @Column(name = "physician", length = 64)
    val physician: String,
    @Column(name = "emp_id", length = 64)
    val empId: String,
    @Column(name = "emp_name", length = 64)
    val empName: String,
    @Column(name = "emp_mobile", length = 64)
    val empMobile: String,
    @OneToMany(mappedBy = "sampleId")
    val sampleExtension: List<SampleExtension>,
    @ManyToOne
    @JoinColumn(name = "item_id")
    val itemId: Item
){
    @ManyToOne
    @JoinColumns(value = [
        JoinColumn(name = "patient_serial", referencedColumnName = "serial", insertable=false, updatable=false),
        JoinColumn(name = "organization_id", referencedColumnName = "organization_id", insertable=false, updatable=false),
        JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable=false, updatable=false)
    ])
    lateinit var patientId: Patient
    @ManyToOne
    @JoinColumn(name = "sample_type_id")
    lateinit var sampleTypeId: SampleType
}
