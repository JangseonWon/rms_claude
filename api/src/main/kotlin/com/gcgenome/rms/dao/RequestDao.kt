package com.gcgenome.rms.dao

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.gcgenome.rms.data.AlisQuery
import com.gcgenome.rms.data.Body
import com.gcgenome.rms.tables.references.*
import org.jooq.DSLContext
import org.jooq.impl.DSL
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalTime

interface RequestDao {
    fun DSLContext.selectBody(alisQuery: AlisQuery): Mono<Body> {
        return Flux.from(
            select(
                REQUEST.CREATE_AT,
                SAMPLE.CREATE_AT,
                SAMPLE.BARCODE,
                PATIENT.NAME,
                PATIENT.SERIAL,
                REQUEST.DEPARTMENT,
                SAMPLE.SAMPLE_TYPE_ID,
                REQUEST.WARD,
                REQUEST.PHYSICIAN,
                SAMPLE.SAMPLING_ON,
                REQUEST.SERVICE_ID,
                PATIENT.SEX,
                PATIENT.BIRTH_YEAR,
                PATIENT.BIRTH_MONTH,
                PATIENT.BIRTH_DAY,
                ORGANIZATION.USER_ID,
                ORGANIZATION.ID,
                ORGANIZATION.NAME,
                ORGANIZATION.REGISTRATION_NUMBER,
                ORGANIZATION.NURSING_NUMBER,
                SAMPLE.USER_SAMPLE_ID,
                DSL.jsonArrayAgg(
                    DSL.jsonObject(
                        DSL.key("id").value(SAMPLE_EXTENSION.EXTENSION_ID),
                        DSL.key("value").value(SAMPLE_EXTENSION.VALUE)
                    )
                ).`as`("etcs")
            ).from(REQUEST)
                .join(SAMPLE).on(REQUEST.SAMPLE_ID.eq(SAMPLE.ID))
                .join(SAMPLE_EXTENSION).on(SAMPLE.ID.eq(SAMPLE_EXTENSION.SAMPLE_ID))
                .join(PATIENT).on(
                    SAMPLE.PATIENT_SERIAL.eq(PATIENT.SERIAL)
                        .and(SAMPLE.ORGANIZATION_ID.eq(PATIENT.ORGANIZATION_ID))
                        .and(SAMPLE.USER_ID.eq(PATIENT.USER_ID))
                ).join(ORGANIZATION).on(
                    PATIENT.ORGANIZATION_ID.eq(ORGANIZATION.ID)
                        .and(PATIENT.USER_ID.eq(ORGANIZATION.USER_ID))
                ).where(
                    REQUEST.CREATE_AT.between(alisQuery.search.startdate.atStartOfDay()).and(alisQuery.search.enddate.atTime(LocalTime.MAX))
                        .and(ORGANIZATION.USER_ID.eq(alisQuery.search.organization))
                )
                .groupBy(
                    REQUEST.CREATE_AT,
                    SAMPLE.CREATE_AT,
                    SAMPLE.BARCODE,
                    PATIENT.NAME,
                    PATIENT.SERIAL,
                    REQUEST.DEPARTMENT,
                    SAMPLE.SAMPLE_TYPE_ID,
                    REQUEST.WARD,
                    REQUEST.PHYSICIAN,
                    SAMPLE.SAMPLING_ON,
                    REQUEST.SERVICE_ID,
                    PATIENT.SEX,
                    PATIENT.BIRTH_YEAR,
                    PATIENT.BIRTH_MONTH,
                    PATIENT.BIRTH_DAY,
                    ORGANIZATION.USER_ID,
                    ORGANIZATION.ID,
                    ORGANIZATION.NAME,
                    ORGANIZATION.REGISTRATION_NUMBER,
                    ORGANIZATION.NURSING_NUMBER,
                    SAMPLE.USER_SAMPLE_ID
                )
                .orderBy(SAMPLE.BARCODE)
        ).map { record ->
            val idno = record.get(PATIENT.BIRTH_YEAR)?.let { year ->
                record.get(PATIENT.BIRTH_MONTH)?.let { month ->
                    record.get(PATIENT.BIRTH_DAY)?.let { day ->
                        "$year${month.toString().padStart(2, '0')}${day.toString().padStart(2, '0')}"
                    }
                }
            }
            val objectMapper = jacksonObjectMapper()
            val etcsJsonString = record.get("etcs", String::class.java)
            val etcs: List<Body.Companion.Etc>? = etcsJsonString?.let(objectMapper::readValue)
            Body.Companion.Request(
                reqdte = record.get(REQUEST.CREATE_AT)?.toLocalDate(),    //의뢰일자
                reqno = record.get(SAMPLE.BARCODE),    //의뢰번호
                patnm = record.get(PATIENT.NAME),    //환자명
                hosno = record.get(PATIENT.SERIAL),    //등록번호
                idno = idno,    //주민등록번호
                hosplc = record.get(REQUEST.DEPARTMENT),    //진료과
                scdev = null,    //시검
                sampcd = record.get(SAMPLE.SAMPLE_TYPE_ID),    //검체코드
                hosloc = record.get(REQUEST.WARD),    //병동
                reqtme = record.get(SAMPLE.CREATE_AT),    //의뢰시간
                mngno = null,    //관리번호
                advyn = null,    //종검여부
                emegyn = null,    //응급여부
                samdte = record.get(SAMPLE.SAMPLING_ON),    //검체채취일
                docnm = record.get(REQUEST.PHYSICIAN),    //주치의
                itemcd = record.get(REQUEST.SERVICE_ID),    //검사코드
                canyn = null,    //취소여부
                organization = record.get(ORGANIZATION.USER_ID), //의뢰요청자
                cstcd = record.get(ORGANIZATION.ID),    //거래처코드
                cstnm = record.get(ORGANIZATION.NAME),    //거래처명
                busno = record.get(ORGANIZATION.REGISTRATION_NUMBER),    //사업자번호
                clicd = record.get(ORGANIZATION.NURSING_NUMBER),    //요양기관번호
                itmamt = null,    //검사금액
                stepri = null,    //의주의뢰가
                sampleno = record.get(SAMPLE.USER_SAMPLE_ID),    //검체번호
                brccd = null,    //영업소코드
                brcnm = null,    //영업소명
                instype = null,    //기관유형
                sampnm = null,    //검체명
                sex = record.get(PATIENT.SEX),    //성별
                etcs = etcs
            )
        }.collectList().map { requests ->
            Body(requests = requests)
        }
    }
}