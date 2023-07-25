package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.dataformat.xml.annotation.*
import com.gcgenome.lims.tables.references.*
import org.jooq.Record
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder("reqdte", "reqno", "patnm", "hosno", "idno", "hosplc", "scdev", "sampcd", "hosloc", "reqtme", "mngno", "advyn", "emegyn", "samdte", "docnm", "itemcd", "canyn", "cstcd", "cstnm", "busno", "clicd", "itmamt", "stepri", "sampleno", "etc", "etcs", "brccd", "brcnm", "empno", "empnm", "mobile", "instype", "sampnm", "barcode")
data class Request (
    var reqdte : Reqdte? = Reqdte(),
    var reqno: Reqno = Reqno(),
    var patnm: Patnm? = Patnm(),
    var hosno: Hosno? = Hosno(),
    var idno: Idno? = Idno(),
    var sex: Sex? = Sex(),
    var hosplc: Hosplc? = Hosplc(),
    var scdev: Scdev? = Scdev(),
    var sampcd: Sampcd? = Sampcd(),
    var hosloc: Hosloc? = Hosloc(),
    var reqtme: Reqtme? = Reqtme(),
    var mngno: Mngno? = Mngno(),
    var advyn: Advyn? = Advyn(),
    var emegyn: Emegyn? = Emegyn(),
    var samdte: Samdte? = Samdte(),
    var docnm: Docnm? = Docnm(),
    var itemcd: Itemcd = Itemcd(),
    var canyn: Canyn? = Canyn(),
    var cstcd: Cstcd = Cstcd(),
    var cstnm: Cstnm? = Cstnm(),
    var busno: Busno? = Busno(),
    var clicd: Clicd? = Clicd(),
    var itmamt: Itmamt? = Itmamt(),
    var stepri: Stepri? = Stepri(),
    var sampleno: Sampleno? = Sampleno(),
    var etc: MutableMap<String, Etc?> = mutableMapOf(),
    var etcs: Etcs? = Etcs(),
    var brccd: Brccd? = Brccd(),
    var brcnm: Brcnm? = Brcnm(),
    var instype: Instype? = Instype(),
    var sampnm: Sampnm? = Sampnm(),
){
    companion object{
        data class Reqdte(
            @field:JacksonXmlText
            val reqdte: String? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val description: String = "의뢰일자"
        )
        data class Cstnm(
            @field:JacksonXmlCData
            @field:JacksonXmlText
            val cstnm: String? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val description: String = "거래처명"
        )
        data class Reqno(
            @field:JacksonXmlText
            val reqno: String? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val description: String = "의뢰번호"
        )
        data class Patnm(
            @field:JacksonXmlText
            val patnm: String? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val description: String = "환자명"
        )
        data class Hosno(
            @field:JacksonXmlText
            val  hosno: String? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val description: String = "등록번호"
        )
        data class Sampleno(
            @field:JacksonXmlText
            val sampleno: String? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val description: String = "검체번호"
        )
        data class Etc(
            @field:JacksonXmlText
            val value: String? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val id: String? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val description: String? = null
        )
        data class Etcs(
            @field:JacksonXmlElementWrapper(useWrapping = false)
            @field:JacksonXmlProperty(localName = "etc")
            val etcs: MutableList<Etc?> = mutableListOf()
        )
        data class Idno(
            @field:JacksonXmlText
            val idno: Int? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val description: String = "주민등록번호"
        )
        data class Sex(
            @field:JacksonXmlText
            val sex: String? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val description: String = "성별"
        )
        data class Hosplc(
            @field:JacksonXmlText
            val hosplc: String? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val description: String = "진료과"
        )
        data class Scdev(
            @field:JacksonXmlText
            val scdev: String? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val description: String = "시검/외상 여부"
        )
        data class Sampcd(
            @field:JacksonXmlText
            val sampcd: String? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val description: String = "검체코드"
        )
        data class Sampnm(
            @field:JacksonXmlText
            val sampnm: String? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val description: String = "검체명"
        )
        data class Hosloc(
            @field:JacksonXmlText
            val hosloc: String? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val description: String = "병동"
        )
        data class Reqtme (
            @field:JacksonXmlText
            val reqtme : String? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val description: String = "의뢰시간"
        )
        data class Mngno (
            @field:JacksonXmlText
            val mngno : String? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val description: String = "관리번호"
        )
        data class Advyn (
            @field:JacksonXmlText
            val advyn : YesOrNo? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val description: String = "종검여부"
        )
        data class Emegyn (
            @field:JacksonXmlText
            val emegyn : YesOrNo? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val description: String = "응급여부"
        )
        data class Samdte (
            @field:JacksonXmlText
            val samdte : String? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val description: String = "검체채취일"
        )
        data class Docnm (
            @field:JacksonXmlCData
            @field:JacksonXmlText
            val docnm : String? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val description: String = "주치의"
        )
        data class Itemcd (
            @field:JacksonXmlText
            val itemcd : String? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val description: String = "검사코드"
        )
        data class Itmamt (
            @field:JacksonXmlText
            val itmamt : String? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val description: String = "검사금액"
        )
        data class Stepri (
            @field:JacksonXmlText
            val stepri : String? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val description: String = "의주의뢰가"
        )
        data class Canyn (
            @field:JacksonXmlText
            val canyn : YesOrNo? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val description: String = "취소여부"
        )
        data class Cstcd (
            @field:JacksonXmlText
            val cstcd : String? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val description: String = "거래처코드"
        )
        data class Busno (
            @field:JacksonXmlText
            val busno : String? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val description: String = "사업자번호"
        )
        data class Clicd (
            @field:JacksonXmlText
            val clicd : String? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val description: String = "요양기관번호"
        )
        data class Brccd (
            @field:JacksonXmlText
            val brccd : String? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val description: String = "영업소코드"
        )
        data class Brcnm (
            @field:JacksonXmlText
            val brcnm : String? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val description: String = "영업소명"
        )
        data class Instype (
            @field:JacksonXmlText
            val instype : String? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val description: String = "기관유형"
        )

        fun mapToRequest(record: Record): Request {
            val reqdte = Reqdte(
                record.getValue(SAMPLE.CREATE_AT)?.toLocalDate().toString().replace("-",""),
                "의뢰일자"
            )
            val reqno = Reqno(
                record.get(SAMPLE.GENOME_BARCODE)?.replace("-",""),
                "의뢰번호"
            )
            val patnm = Patnm(
                record.get(PATIENT.NAME),
                "환자명"
            )
            val hosno = Hosno(
                record.get(PATIENT.SERIAL),
                "등록번호"
            )
            val year = record.get(PATIENT.BIRTH_YEAR)
            val month = record.get(PATIENT.BIRTH_MONTH)
            val day = record.get(PATIENT.BIRTH_DAY)

            val idnoValue = year.toString() + month.toString() + day.toString()

            val idno = Idno(
                idnoValue.toInt(),
                "주민등록번호"
            )

            val sex = Sex(
                record.get(PATIENT.SEX),
                "성별"
            )
            val hosplc = Hosplc(
                record.get(SAMPLE.DEPARTMENT),
                "진료과"
            )
            val scdev = Scdev(
                record.get(ORDER.TEST).toString() + "/" + record.get(ORDER.CREDIT),
                "시검/외상 여부"
            )
            val sampcd = Sampcd(
                record.get(SAMPLE.SAMPLE_TYPE_ID),
                "검체코드"
            )
            val hosloc = Hosloc(
                record.get(SAMPLE.WARD),
                "병동"
            )
            val reqtme = Reqtme(
                record.get(SAMPLE.CREATE_AT)?.toLocalTime()?.format(DateTimeFormatter.ofPattern("HH:mm")).toString(),
                "의뢰시간"
            )
            val mngno = Mngno(
                null,
                "관리번호"
            )
            val advyn = Advyn(
                YesOrNo.N,
                "종검여부"
            )
            val emegyn = Emegyn(
                YesOrNo.N,
                "응급여부"
            )
            val samdte = Samdte(
                record.get(SAMPLE.SAMPLING)?.toLocalDate().toString().replace("-",""),
                "검체채취일"
            )
            val docnm = Docnm(
                record.get(SAMPLE.PHYSICIAN),
                "주치의"
            )
            val itemcd = Itemcd(
                record.get(ITEM.SERVICE_ID),
                "검사코드"
            )
            val canyn = Canyn(
                YesOrNo.N,
                "취소여부"
            )
            val cstcd = Cstcd(
                record.get(ORGANIZATION.ID),
                "거래처코드"
            )
            val cstnm = Cstnm(
                record.get(ORGANIZATION.NAME),
                "거래처명"
            )
            val busno = Busno(
                record.get(ORGANIZATION.REGISTRATION_NUMBER),
                "사업자번호"
            )
            val clicd = Clicd(
                record.get(ORGANIZATION.NURSING_NUMBER),
                "요양기관번호"
            )
            val itmamt = Itmamt(
                record.get(ORDER.PRICE).toString(),
                "검사금액"
            )
            val stepri = Stepri(
                record.get(ORDER.OUTSOURCING_COST).toString(),
                "의주의뢰가"
            )
            val sampleno = Sampleno(
                record.get(SAMPLE.GENOME_BARCODE)?.substring(8)?.replace("-",""),
                "검체번호"
            )
            val etc: MutableMap<String, Etc?> = mutableMapOf()
            val etcs = Etcs()
            val brccd = Brccd(
                record.get(ORGANIZATION.BRANCH_ID),
                "영업소코드"
            )
            val brcnm = Brcnm(
                record.get(ORGANIZATION.BRANCH_NAME),
                "영업소명"
            )
            val instype = Instype(
                record.get(ORGANIZATION.TYPE),
                "기관유형"
            )
            val sampnm = Sampnm(
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
                    instype = instype,
                    sampnm = sampnm,
            )
        }
    }
}
