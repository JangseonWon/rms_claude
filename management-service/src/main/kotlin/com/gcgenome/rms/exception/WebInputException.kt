package com.gcgenome.rms.exception

import org.springframework.web.server.ServerWebInputException

class WebInputException: ServerWebInputException("누락된 정보 또는 잘못입력된 정보가 있습니다.")