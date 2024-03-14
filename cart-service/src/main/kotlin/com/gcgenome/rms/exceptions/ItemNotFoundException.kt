package com.gcgenome.rms.exceptions

class ItemNotFoundException(sampleId: String): RuntimeException("요청한 '${sampleId}'가 존재하지 않습니다.")