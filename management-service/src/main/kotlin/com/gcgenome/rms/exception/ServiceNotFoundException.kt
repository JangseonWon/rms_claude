package com.gcgenome.rms.exception

class ServiceNotFoundException(serviceId: String): RuntimeException("요청한 서비스 ${serviceId}를 찾을 수 없습니다.")