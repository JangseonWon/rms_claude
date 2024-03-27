package com.gcgenome.rms.exceptions

class RequestNotFoundException(serial:String, serviceId:String, barcode:String) : RuntimeException("요청한 의뢰 ('${serial}','${serviceId}','${barcode}') 를 찾을 수 없습니다.") {
}