package com.gcgenome.rms.service

import com.gcgenome.rms.data.Request
import com.gcgenome.rms.data.YesOrNo
import com.gcgenome.rms.tables.references.*
import org.jooq.DSLContext
import org.jooq.Record
import java.time.format.DateTimeFormatter

class RequestMap (
    val dslContext: DSLContext
): OrganizationDao{
    fun mapToRequest(record: Record, mainName: String, subName: String): Request {
        val reqdte = Request.Companion.Reqdte(
            record.getValue(SAMPLE.CREATE_AT)?.toLocalDate().toString().replace("-", ""),
            "의뢰일자"
        )
        val reqno = Request.Companion.Reqno(
            record.get(SAMPLE.GENOME_BARCODE)?.replace("-", ""),
            "의뢰번호"
        )
        val patnm = Request.Companion.Patnm(
            record.get(PATIENT.NAME),
            "환자명"
        )
        val hosno = Request.Companion.Hosno(
            record.get(PATIENT.SERIAL),
            "등록번호"
        )
        val year = record.get(PATIENT.BIRTH_YEAR)
        val month = record.get(PATIENT.BIRTH_MONTH)
        val day = record.get(PATIENT.BIRTH_DAY)

        val idnoValue = year.toString() + month.toString() + day.toString()

        val idno = Request.Companion.Idno(
            idnoValue.toInt(),
            "주민등록번호"
        )

        val sex = Request.Companion.Sex(
            record.get(PATIENT.SEX),
            "성별"
        )
        val hosplc = Request.Companion.Hosplc(
            record.get(SAMPLE.DEPARTMENT),
            "진료과"
        )
        val scdev = Request.Companion.Scdev(
            record.get(ORDER.TEST).toString() + "/" + record.get(ORDER.CREDIT),
            "시검/외상 여부"
        )
        val sampcd = Request.Companion.Sampcd(
            record.get(SAMPLE.SAMPLE_TYPE_ID),
            "검체코드"
        )
        val hosloc = Request.Companion.Hosloc(
            record.get(SAMPLE.WARD),
            "병동"
        )
        val reqtme = Request.Companion.Reqtme(
            record.get(SAMPLE.CREATE_AT)?.toLocalTime()?.format(DateTimeFormatter.ofPattern("HH:mm")).toString(),
            "의뢰시간"
        )
        val mngno = Request.Companion.Mngno(
            null,
            "관리번호"
        )
        val advyn = Request.Companion.Advyn(
            YesOrNo.N,
            "종검여부"
        )
        val emegyn = Request.Companion.Emegyn(
            YesOrNo.N,
            "응급여부"
        )
        val samdte = Request.Companion.Samdte(
            record.get(SAMPLE.SAMPLING)?.toLocalDate().toString().replace("-", ""),
            "검체채취일"
        )
        val docnm = Request.Companion.Docnm(
            record.get(SAMPLE.PHYSICIAN),
            "주치의"
        )
        val itemcd = Request.Companion.Itemcd(
            record.get(ITEM.SERVICE_ID),
            "검사코드"
        )
        val canyn = Request.Companion.Canyn(
            YesOrNo.N,
            "취소여부"
        )
        val cstcd = Request.Companion.Cstcd(
            record.get(ORGANIZATION.ID),
            "거래처코드"
        )

        val cstnm = if (mainName == subName) {
            Request.Companion.Cstnm(
                Request.Companion.OrganizationMain(mainName),
                null,
                "거래처명"
            )
        } else {
            Request.Companion.Cstnm(
                Request.Companion.OrganizationMain(mainName),
                Request.Companion.OrganizationSub(subName),
                "거래처명"
            )
        }

        val busno = Request.Companion.Busno(
            record.get(ORGANIZATION.REGISTRATION_NUMBER),
            "사업자번호"
        )
        val clicd = Request.Companion.Clicd(
            record.get(ORGANIZATION.NURSING_NUMBER),
            "요양기관번호"
        )
        val itmamt = Request.Companion.Itmamt(
            record.get(ORDER.PRICE).toString(),
            "검사금액"
        )
        val stepri = Request.Companion.Stepri(
            record.get(ORDER.OUTSOURCING_COST).toString(),
            "의주의뢰가"
        )
        val sampleno = Request.Companion.Sampleno(
            record.get(SAMPLE.GENOME_BARCODE)?.substring(8)?.replace("-", ""),
            "검체번호"
        )
        val etc: MutableMap<String, Request.Companion.Etc?> = mutableMapOf()
        val etcs = Request.Companion.Etcs()
        val brccd = Request.Companion.Brccd(
            record.get(ORGANIZATION.BRANCH_ID),
            "영업소코드"
        )
        val brcnm = Request.Companion.Brcnm(
            record.get(ORGANIZATION.BRANCH_NAME),
            "영업소명"
        )
        val empno = Request.Companion.Empno(
            record.get(SAMPLE.EMP_ID),
            "영업사원사번"
        )
        val empnm = Request.Companion.Empnm(
            record.get(SAMPLE.EMP_NAME),
            "영업사원명"
        )
        val mobile = Request.Companion.Mobile(
            record.get(SAMPLE.EMP_MOBILE),
            "영업사원핸드폰번호"
        )
        val instype = Request.Companion.Instype(
            record.get(ORGANIZATION.TYPE),
            "기관유형"
        )
        val sampnm = Request.Companion.Sampnm(
            record.get(SAMPLE_TYPE.NAME),
            "검체명"
        )

        return Request(
            reqdte = reqdte,
            reqno = reqno,
            patnm = patnm,
            hosno = hosno,
            idno = idno,
            sex = sex,
            hosplc = hosplc,
            scdev = scdev,
            sampcd = sampcd,
            hosloc = hosloc,
            reqtme = reqtme,
            mngno = mngno,
            advyn = advyn,
            emegyn = emegyn,
            samdte = samdte,
            docnm = docnm,
            itemcd = itemcd,
            canyn = canyn,
            cstcd = cstcd,
            cstnm = cstnm,
            busno = busno,
            clicd = clicd,
            itmamt = itmamt,
            stepri = stepri,
            sampleno = sampleno,
            etc = etc,
            etcs = etcs,
            brccd = brccd,
            brcnm = brcnm,
            empno = empno,
            empnm = empnm,
            mobile = mobile,
            instype = instype,
            sampnm = sampnm
        )
    }
}