package com.gcgenome.rms.exception

class ServiceAlreadyExistsException(serviceId: String): RuntimeException("요청한 서비스 ${serviceId}가 이미 존재합니다.")