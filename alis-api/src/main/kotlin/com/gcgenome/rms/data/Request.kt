package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText

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
    var empno: Empno? = Empno(),
    var empnm: Empnm? = Empnm(),
    var mobile: Mobile? = Mobile(),
    var instype: Instype? = Instype(),
    var sampnm: Sampnm? = Sampnm(),
) {
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
            val main: OrganizationMain? = null,
            @field:JacksonXmlCData
            @field:JacksonXmlText
            val sub: OrganizationSub? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val description: String = "거래처명"
        )
        data class OrganizationMain(
            @field:JacksonXmlCData
            @field:JacksonXmlText
            val main: String? = null
        )
        data class OrganizationSub(
            @field:JacksonXmlCData
            @field:JacksonXmlText
            val sub: String? = null
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
        data class Empno (
            @field:JacksonXmlText
            val empno : String? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val description: String = "영업사원사번"
        )
        data class Empnm (
            @field:JacksonXmlText
            val empnm : String? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val description: String = "영업사원명"
        )
        data class Mobile (
            @field:JacksonXmlText
            val brcnm : String? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val description: String = "영업사원핸드폰번호"
        )
        data class Instype (
            @field:JacksonXmlText
            val instype : String? = null,
            @field:JacksonXmlProperty(isAttribute = true)
            val description: String = "기관유형"
        )
    }
}
