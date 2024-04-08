package com.gcgenome.rms.exception

class ExtensionIdNotFoundException(serviceId: String, extensionId: String): RuntimeException("서비스'${serviceId}'의 추가정보 '${extensionId}' 아이디를 찾을 수 없습니다. 확인 바랍니다.")