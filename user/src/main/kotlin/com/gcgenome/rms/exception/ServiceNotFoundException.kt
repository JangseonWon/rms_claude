package com.gcgenome.rms.exception

class ServiceNotFoundException(itemId: String): RuntimeException("요청한 서비스 ${itemId}를 찾을 수 없습니다.")