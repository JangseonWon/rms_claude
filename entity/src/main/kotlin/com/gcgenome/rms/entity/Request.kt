package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(schema = "rms_dev", name = "request")
data class Request(
    @EmbeddedId
    val pk: RequestPK,
    @Column(name = "user_service_id", length = 64, nullable = false)
    val userServiceId: String,
    @Column(name = "status", length = 64, nullable = false)
    @Enumerated(EnumType.STRING)
    val status: RequestStatus,
    @Column(name = "memo", nullable = true)
    val memo: String,
    @Column(name = "department", length = 64, nullable = true)
    val department: String,
    @Column(name = "ward", length = 64, nullable = true)
    val ward: String,
    @Column(name = "physician", length = 64, nullable = true)
    val physician: String,
    @Column(name = "courier_company", length = 64, nullable = true)
    val courierCompany: String,
    @Column(name = "awb_number", length = 64, nullable = true)
    val awbNumber: String,
    @Column(name = "create_at", nullable = true)
    val createAt: LocalDateTime,
    @Column(name = "cart_at", nullable = true)
    val cartAt: LocalDateTime,
    @Column(name = "specified_at", nullable = true)
    val specifiedAt: LocalDateTime,
    @Column(name = "complete_at", nullable = true)
    val completeAt: LocalDateTime,
    @Column(name =" reported_at", nullable = true)
    val reportedAt: LocalDateTime,
    @Column(name = "resample_at", nullable = true)
    val resampleAt: LocalDateTime,
    @Column(name = "last_modify_at", nullable = false)
    val lastModifyAt: LocalDateTime,

    @OneToMany(mappedBy = "requestId")
    val report: List<Report>,

    @ManyToOne
    @JoinColumn(name = "service_id", insertable = false, updatable = false)
    val serviceId: Service,
    @ManyToOne
    @JoinColumn(name = "sample_id", insertable = false, updatable = false)
    val sampleId: Sample,
    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false, nullable = false)
    val userId: User,
    @ManyToOne
    @JoinColumn(name = "request_group_id", insertable = false, updatable = false, nullable = false)
    val requestGroupId: RequestGroup,
    @ManyToOne
    @JoinColumn(name = "request_relation_id", insertable = false, updatable = false, nullable = false)
    val requestRelationId: RequestRelation

){
    @Embeddable
    data class RequestPK (
        @Column(name = "service_id") val serviceId: String,
        @Column(name = "sample_id") val sampleId: UUID
    ) : Serializable

    enum class RequestStatus {
        TOTAL,
        CART,
        UNCONFIRMED_ORDER,
        COMPLETED_ORDER,
        IN_PROGRESS,
        TEST_FAILED,
        DELIVERED,
        COMPLETED
    }
}