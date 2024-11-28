package com.gcgenome.rms.dao

import com.gcgenome.rms.authentication.User
import com.gcgenome.rms.data.Page
import com.gcgenome.rms.data.PatientDTO
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.tables.references.ORGANIZATION
import com.gcgenome.rms.tables.references.PATIENT
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import reactor.core.publisher.Mono

interface PatientDao: QueryDao{
    fun DSLContext.insertPatient(userId: String, patient: PatientDTO): Mono<PatientDTO> {
        return Mono.from(
            insertInto(PATIENT)
                .set(PATIENT.ORGANIZATION_ID, patient.organization!!.id)
                .set(PATIENT.SERIAL, patient.serial)
                .set(PATIENT.USER_ID, userId)
                .set(PATIENT.BIRTH_DAY, patient.birthDay)
                .set(PATIENT.BIRTH_MONTH, patient.birthMonth)
                .set(PATIENT.BIRTH_YEAR, patient.birthYear)
                .set(PATIENT.NAME, patient.name)
                .set(PATIENT.SEX, patient.sex)
                .onDuplicateKeyUpdate()
                .set(PATIENT.NAME, patient.name)
                .set(PATIENT.SEX, patient.sex)
                .set(PATIENT.BIRTH_DAY, patient.birthDay)
                .set(PATIENT.BIRTH_MONTH, patient.birthMonth)
                .set(PATIENT.BIRTH_YEAR, patient.birthYear)
                .returning()
        ).map { it.into(PatientDTO::class.java) }
    }

    fun DSLContext.selectPatientsWithPage(query: Query, user: User): Mono<Page<PatientDTO>> {
        val joins = listOf(
            QueryDao.JoinInfo(
                ORGANIZATION,
                PATIENT.ORGANIZATION_ID.eq(ORGANIZATION.ID)
                    .and(PATIENT.USER_ID.eq(ORGANIZATION.USER_ID))
                , QueryDao.JoinType.LEFT
            )
        )
        val fields = listOf(
            PATIENT.SERIAL,
            PATIENT.NAME,
            PATIENT.SEX,
            PATIENT.BIRTH_YEAR,
            PATIENT.BIRTH_MONTH,
            PATIENT.BIRTH_DAY,
            jsonObject(
                key("id").value(ORGANIZATION.ID),
                key("name").value(ORGANIZATION.NAME),
                key("registration_number").value(ORGANIZATION.REGISTRATION_NUMBER),
                key("type").value(ORGANIZATION.TYPE),
                key("nursing_number").value(ORGANIZATION.NURSING_NUMBER)
            ).`as`("organization")
        )
        val condition = if (user.role == "USER") {
            PATIENT.USER_ID.eq(user.id)
        } else noCondition()

        return selectPage(mainTable = PATIENT, query= query, joinTables = joins, selectFields = fields, where = condition) {record ->
            record.into(PatientDTO::class.java)
        }
    }
}
