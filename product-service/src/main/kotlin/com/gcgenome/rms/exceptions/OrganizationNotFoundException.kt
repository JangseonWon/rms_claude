package com.gcgenome.rms.exceptions

class OrganizationNotFoundException(userId: String, organizationId: String): RuntimeException("사용자'${userId}'의 기관 '${organizationId}' 명을 찾을 수 없습니다. 확인 바랍니다.")