package com.gcgenome.rms.exceptions

import org.springframework.web.server.ServerWebInputException

class ServerInputException: ServerWebInputException("누락된 정보 또는 잘못입력된 정보가 있습니다.")