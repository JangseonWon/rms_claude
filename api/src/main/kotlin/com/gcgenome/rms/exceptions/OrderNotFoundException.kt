package com.gcgenome.rms.exceptions

class OrderNotFoundException(serial: String): RuntimeException("요청 '${serial}'를 찾을 수 없습니다.")