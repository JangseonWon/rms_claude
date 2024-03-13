package com.gcgenome.rms.exceptions

import java.util.*

class SampleNotFoundException(sampleId: UUID): RuntimeException("요청한 검체 '${sampleId}'를 찾을 수 없습니다.")