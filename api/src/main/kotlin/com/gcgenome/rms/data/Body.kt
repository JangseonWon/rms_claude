package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText
import java.time.LocalDate
import java.time.LocalDateTime

data class Body(
    @JacksonXmlElementWrapper(localName = "requests")
    @JacksonXmlProperty(localName = "request")
    val requests: List<Request>?
){
    companion object{
        data class Request(
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
            val reqdte: LocalDate?,    //의뢰일자
            val reqno: String?,    //의뢰번호
            val patnm: String?,    //환자명
            val hosno: String?,    //등록번호
            val idno: String?,    //주민등록번호
            val hosplc: String?,    //진료과
            val scdev: String?,    //시검
            val sampcd: String?,    //검체코드
            val hosloc: String?,    //병동
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
            val reqtme: LocalDateTime?,    //의뢰시간
            val mngno: String?,    //관리번호
            val advyn: String?,    //종검여부
            val emegyn: String?,    //응급여부
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
            val samdte: LocalDate?,    //검체채취일
            val docnm: String?,    //주치의
            val itemcd: String?,    //검사코드
            val canyn: String?,    //취소여부
            val organization: String?,    //의뢰요청자
            val cstcd: String?,    //거래처코드
            @JacksonXmlCData
            val cstnm: String?,    //거래처명
            val busno: String?,    //사업자번호
            val clicd: String?,    //요양기관번호
            val itmamt: String?,    //검사금액
            val stepri: String?,    //의주의뢰가
            val sampleno: String?,    //검체번호
            val brccd: String?,    //영업소코드
            val brcnm: String?,    //영업소명
            val instype: String?,    //기관유형
            val sampnm: String?,    //검체명
            val sex: String?,    //성별
            @JacksonXmlElementWrapper(localName = "etcs")
            @JacksonXmlProperty(localName = "etc")
            val etcs: List<Etc>?
        )
        data class Etc (
            @JacksonXmlText
            val value: String,
            @JacksonXmlProperty(isAttribute = true)
            val id: String,
        )

    }
}