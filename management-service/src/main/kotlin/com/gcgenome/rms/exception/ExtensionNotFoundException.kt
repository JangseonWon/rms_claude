package com.gcgenome.rms.exception

class ExtensionNotFoundException(extensionId: String): RuntimeException("${extensionId}를 찾을 수 없습니다.")