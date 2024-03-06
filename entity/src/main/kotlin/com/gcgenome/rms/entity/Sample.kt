package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(schema = "rms_dev", name = "sample")
data class Sample(
    @Id @Column(name = "id")
    val id: UUID,
    @Column(name = "status", length = 64, nullable = false)
    val status: String,
    @Column(name = "barcode", length = 64, nullable = true)
    val barcode: String,
    @Column(name = "serial", nullable = true)
    val serial: String,
    @Column(name = "quantity", nullable = false)
    val quantity: Int,
    @Column(name = "age", nullable = true)
    val age: Int,
    @Column(name = "retest_reason", nullable = true)
    val retestReason: String,
    @Column(name = "memo", nullable = true)
    val memo: String,
    @Column(name = "department", length = 64, nullable = true)
    val department: String,
    @Column(name = "ward", length = 64, nullable = true)
    val ward: String,
    @Column(name = "physician", length = 64, nullable = true)
    val physician: String,
    @Column(name = "sampling", nullable = false)
    val sampling: LocalDateTime,
    @Column(name = "create_at", nullable = true)
    val createAt: LocalDateTime,
    @Column(name = "specified_at", nullable = true)
    val specifiedAt: LocalDateTime,
    @Column(name = "complete_at", nullable = true)
    val completeAt: LocalDateTime,
    @Column(name = "resample_at", nullable = true)
    val resampleAt: LocalDateTime,
    @Column(name = "last_modify_at", nullable = false)
    val lastModifyAt: LocalDateTime,
    @Column(name = "emp_id", length = 64, nullable = true)
    val empId: String,
    @Column(name = "emp_name", length = 64, nullable = true)
    val empName: String,
    @Column(name = "emp_mobile", length = 64, nullable = true)
    val empMobile: String,
    @Column(name = "labs_test", nullable = true)
    val labsTest: Boolean,
    @Column(name = "labs_credit", nullable = true)
    val labsCredit: Boolean,
    @Column(name = "labs_price", nullable = true)
    val labsPrice: Int,
    @Column(name = "labs_outsourcing_cost", nullable = true)
    val labsOutsourcingCost: Int,

    @OneToMany(mappedBy = "sampleId")
    val sampleExtension: List<SampleExtension>,
    @OneToMany(mappedBy = "sampleId")
    val report: List<Report>,

    @ManyToOne
    @JoinColumns(value = [
        JoinColumn(name = "order_id", referencedColumnName = "order_id", insertable = false, updatable = false, nullable = false)
        , JoinColumn(name = "service_id", referencedColumnName = "service_id", insertable = false, updatable = false, nullable = false)
    ])
    val itemId: Item,
    @ManyToOne
    @JoinColumn(name = "sample_type_id", insertable = false, updatable = false, nullable = false)
    val sampleTypeId: SampleType,
    @ManyToOne
    @JoinColumns(value = [
        JoinColumn(name = "patient_serial", referencedColumnName = "serial", insertable=false, updatable=false),
        JoinColumn(name = "organization_id", referencedColumnName = "organization_id", insertable=false, updatable=false),
        JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable=false, updatable=false)
    ])
    val patientId: Patient
)
