package com.gcgenome.rms.exceptions

class ServiceNotFoundException(serviceId: String): RuntimeException("요청한 검사 '${serviceId}'를 등록할 수 없습니다. 검사 항목 확인 바랍니다.")