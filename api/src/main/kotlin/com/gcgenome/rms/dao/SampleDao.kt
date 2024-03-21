package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Sample
import com.gcgenome.rms.data.SampleSearchCondition
import com.gcgenome.rms.tables.records.SampleRecord
import com.gcgenome.rms.tables.references.*
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortOrder
import org.jooq.TableField
import org.jooq.impl.DSL.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface SampleDao {
    fun DSLContext.selectSampleByBarcode(barcode: String): Mono<Sample> =
        Mono.from(selectFrom(SAMPLE).where(SAMPLE.BARCODE.eq(barcode))).map { it.into(Sample::class.java) }

    fun DSLContext.selectSampleById(sampleId: UUID) : Mono<Sample> {
        return Mono.from(
            select(
                SAMPLE.BARCODE,
                SAMPLE.SERVICE_ID,
                SAMPLE.STATUS,
                SAMPLE.SERIAL,
                SAMPLE.QUANTITY,
                SAMPLE.AGE,
                SAMPLE.MEMO,
                SAMPLE.DEPARTMENT,
                SAMPLE.WARD,
                SAMPLE.PHYSICIAN,
                SAMPLE.SAMPLING,
                SAMPLE.CREATE_AT,
                SAMPLE.SPECIFIED_AT,
                SAMPLE.COMPLETE_AT,
                SAMPLE.RESAMPLE_AT,
                SAMPLE.LAST_MODIFY_AT,
                SAMPLE.EMP_ID,
                SAMPLE.EMP_NAME,
                SAMPLE.EMP_MOBILE,
                SAMPLE.LABS_TEST,
                SAMPLE.LABS_CREDIT,
                SAMPLE.LABS_PRICE,
                SAMPLE.LABS_OUTSOURCING_COST,
                SAMPLE.SAMPLE_TYPE_ID,
                jsonObject(
                    key("serial").value(PATIENT.SERIAL),
                    key("name").value(PATIENT.NAME),
                    key("sex").value(PATIENT.SEX),
                    key("name").value(PATIENT.NAME),
                    key("birth_year").value(PATIENT.BIRTH_YEAR),
                    key("birth_month").value(PATIENT.BIRTH_MONTH),
                    key("birth_day").value(PATIENT.BIRTH_DAY),
                    key("organization").value(
                        select(
                            jsonObject(
                                key("id").value(ORGANIZATION.ID),
                                key("name").value(ORGANIZATION.NAME),
                                key("type").value(ORGANIZATION.TYPE),
                                key("registration_number").value(ORGANIZATION.REGISTRATION_NUMBER),
                                key("nursing_number").value(ORGANIZATION.NURSING_NUMBER)
                            )
                        ).from(ORGANIZATION)
                            .where(PATIENT.ORGANIZATION_ID.eq(ORGANIZATION.ID)
                                .and(PATIENT.USER_ID.eq(ORGANIZATION.USER_ID))
                                .and(ORGANIZATION.USER_ID.ne(ORGANIZATION.ID)))
                    )
                ).`as`("patient"),
                field(
                    select(
                        jsonArrayAgg(jsonObject(
                            key("id").value(SAMPLE_EXTENSION.EXTENSION_ID),
                            key("value").value(SAMPLE_EXTENSION.VALUE)
                        ))
                    ).from(SAMPLE_EXTENSION).where(SAMPLE.ID.eq(SAMPLE_EXTENSION.SAMPLE_ID))
                ).`as`("extensions"),
                field(
                    select(
                        jsonArrayAgg(jsonObject(
                            key("create_at").value(REPORT.CREATE_AT),
                            key("reported_at").value(REPORT.REPORTED_AT),
                            key("type").value(REPORT.TYPE),
                            key("value").value(REPORT.VALUE)
                        ))
                    ).from(REPORT).where(SAMPLE.ID.eq(REPORT.SAMPLE_ID))
                ).`as`("reports")
            ).from(SAMPLE)
                .join(PATIENT).on(SAMPLE.PATIENT_SERIAL.eq(PATIENT.SERIAL)
                    .and(SAMPLE.ORGANIZATION_ID.eq(PATIENT.ORGANIZATION_ID))
                    .and(SAMPLE.USER_ID.eq(PATIENT.USER_ID)))
                .where(SAMPLE.ID.eq(sampleId))
        ).map { it.into(Sample::class.java) }

    }

    fun DSLContext.selectSampleByCondition(condition: SampleSearchCondition, whereClause:Condition): Flux<Sample> {
        val order : TableField<SampleRecord,out Any?> = when (condition.sort?.field) {
            "create_at" -> SAMPLE.CREATE_AT
            "id" -> SAMPLE.BARCODE
            "status" -> SAMPLE.STATUS
            "serial" -> SAMPLE.SERIAL
            "sample_type" -> SAMPLE.SAMPLE_TYPE_ID
            "emp_id" -> SAMPLE.EMP_ID
            "emp_name" -> SAMPLE.EMP_NAME
            "test" -> SAMPLE.LABS_TEST
            else -> SAMPLE.CREATE_AT
        }

        val asc: SortOrder = when (condition.sort?.asc) {
            true -> SortOrder.ASC
            false -> SortOrder.DESC
            else -> SortOrder.DEFAULT
        }

        return Flux.from(
            select(
                SAMPLE.BARCODE,
                SAMPLE.SERVICE_ID,
                SAMPLE.STATUS,
                SAMPLE.SERIAL,
                SAMPLE.QUANTITY,
                SAMPLE.AGE,
                SAMPLE.MEMO,
                SAMPLE.DEPARTMENT,
                SAMPLE.WARD,
                SAMPLE.PHYSICIAN,
                SAMPLE.SAMPLING,
                SAMPLE.CREATE_AT,
                SAMPLE.SPECIFIED_AT,
                SAMPLE.COMPLETE_AT,
                SAMPLE.RESAMPLE_AT,
                SAMPLE.LAST_MODIFY_AT,
                SAMPLE.EMP_ID,
                SAMPLE.EMP_NAME,
                SAMPLE.EMP_MOBILE,
                SAMPLE.LABS_TEST,
                SAMPLE.LABS_CREDIT,
                SAMPLE.LABS_PRICE,
                SAMPLE.LABS_OUTSOURCING_COST,
                SAMPLE.SAMPLE_TYPE_ID,
                jsonObject(
                    key("serial").value(PATIENT.SERIAL),
                    key("name").value(PATIENT.NAME),
                    key("sex").value(PATIENT.SEX),
                    key("name").value(PATIENT.NAME),
                    key("birth_year").value(PATIENT.BIRTH_YEAR),
                    key("birth_month").value(PATIENT.BIRTH_MONTH),
                    key("birth_day").value(PATIENT.BIRTH_DAY),
                    key("organization").value(
                        select(
                            jsonObject(
                                key("id").value(ORGANIZATION.ID),
                                key("name").value(ORGANIZATION.NAME),
                                key("type").value(ORGANIZATION.TYPE),
                                key("registration_number").value(ORGANIZATION.REGISTRATION_NUMBER),
                                key("nursing_number").value(ORGANIZATION.NURSING_NUMBER)
                            )
                        ).from(ORGANIZATION)
                            .where(PATIENT.ORGANIZATION_ID.eq(ORGANIZATION.ID)
                                .and(PATIENT.USER_ID.eq(ORGANIZATION.USER_ID))
                                .and(ORGANIZATION.USER_ID.ne(ORGANIZATION.ID)))
                    )
                ).`as`("patient"),
                field(
                    select(
                        jsonArrayAgg(jsonObject(
                            key("id").value(SAMPLE_EXTENSION.EXTENSION_ID),
                            key("value").value(SAMPLE_EXTENSION.VALUE)
                        ))
                    ).from(SAMPLE_EXTENSION).where(SAMPLE.ID.eq(SAMPLE_EXTENSION.SAMPLE_ID))
                ).`as`("extensions"),
                field(
                    select(
                        jsonArrayAgg(jsonObject(
                            key("create_at").value(REPORT.CREATE_AT),
                            key("reported_at").value(REPORT.REPORTED_AT),
                            key("type").value(REPORT.TYPE),
                            key("value").value(REPORT.VALUE)
                        ))
                    ).from(REPORT).where(SAMPLE.ID.eq(REPORT.SAMPLE_ID))
                ).`as`("reports")
            ).from(SAMPLE)
                .join(PATIENT).on(SAMPLE.PATIENT_SERIAL.eq(PATIENT.SERIAL)
                    .and(SAMPLE.ORGANIZATION_ID.eq(PATIENT.ORGANIZATION_ID))
                    .and(SAMPLE.USER_ID.eq(PATIENT.USER_ID)))
                .where(whereClause)
                .orderBy(order.sort(asc))
        ).map { it.into(Sample::class.java) }
    }

    fun DSLContext.deleteSampleById(sampleId: UUID): Mono<Sample> {
        return Mono.from(deleteFrom(SAMPLE).where(SAMPLE.ID.eq(sampleId)).returning()).map { it.into(Sample::class.java) }
    }

}