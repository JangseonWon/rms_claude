package com.gcgenome.rms.exceptions

class CategoryNotFoundException(serviceId: String): RuntimeException("요청한 카테고리 '${serviceId}'를 찾을 수 없습니다. 항목 확인 바랍니다.")