package com.gcgenome.rms.exceptions

import java.util.*

class SampleNotFoundException(barcode: String): RuntimeException("요청한 검체 '${barcode}'를 찾을 수 없습니다.")