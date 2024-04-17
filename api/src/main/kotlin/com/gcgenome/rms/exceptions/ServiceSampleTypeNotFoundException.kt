package com.gcgenome.rms.exceptions

class ServiceSampleTypeNotFoundException(serviceId: String, sampleType: String): RuntimeException("검사 '${serviceId}'에 요청한 검체 '${sampleType}'를 찾을 수 없습니다. 해당 검사에 등록 가능한 검체타입 확인 바랍니다.")