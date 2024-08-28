package com.gcgenome.rms.repository

import com.gcgenome.rms.entity.*
import com.gcgenome.rms.model.LabSampleCodeDTO
import com.gcgenome.rms.model.LabTestCodeDTO
import com.gcgenome.rms.model.OrganizationDTO
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class AlisRepo(
    private val jpaQueryFactory: JPAQueryFactory,
) {
    fun findService(): List<LabTestCodeDTO> {
        val labTestCode = QLabTestCode.labTestCode
        return jpaQueryFactory
            .select(Projections.constructor(
                LabTestCodeDTO::class.java,
                labTestCode.testCode,
                labTestCode.testDisplayName
            ))
            .from(labTestCode)
            .where(labTestCode.isTestSub.eq(false)
                .and(labTestCode.isTestUse.eq(true)))
            .fetch()
    }
    fun findSampleType(): List<LabSampleCodeDTO> {
        val labSampleCode = QLabSampleCode.labSampleCode
        return jpaQueryFactory
            .select(Projections.constructor(
                LabSampleCodeDTO::class.java,
                labSampleCode.sampleCode,
                labSampleCode.sampleFullName
            ))
            .from(labSampleCode)
            .fetch()
    }
    fun findOrganizations(): List<OrganizationDTO> {
        val progCompCode = QProgCompCode.progCompCode
        val progCompMngCode = QProgCompMngCode.progCompMngCode

        return jpaQueryFactory
            .select(Projections.constructor(
                OrganizationDTO::class.java,
                progCompCode.compCode,
                progCompCode.compName,
                progCompMngCode.compMngBeginNo
            ))
            .from(progCompCode)
            .join(progCompMngCode).on(progCompCode.compMngCode.eq(progCompMngCode.compMngCode))
            .fetch()
    }
}