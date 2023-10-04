package com.gcgenome.rms.exceptions

import java.util.*

class ItemNotFoundException(sampleId: UUID): RuntimeException("요청한 품목 '${sampleId}'를 찾을 수 없습니다.")