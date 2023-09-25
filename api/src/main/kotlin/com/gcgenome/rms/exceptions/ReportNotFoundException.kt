package com.gcgenome.rms.exceptions

import java.util.*

class ReportNotFoundException(reportId: UUID) :RuntimeException("요청한 결과 '${reportId}'의 결과를 찾을 수 없습니다.")
