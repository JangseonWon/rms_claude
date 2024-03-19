package com.gcgenome.rms.exception

import org.springframework.web.server.ServerWebInputException

class OrganizationNotFoundException: ServerWebInputException("기관ID가 잘못 입력되었습니다.")
