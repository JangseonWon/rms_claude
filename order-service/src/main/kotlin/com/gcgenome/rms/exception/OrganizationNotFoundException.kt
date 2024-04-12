package com.gcgenome.rms.exception

class OrganizationNotFoundException : RuntimeException("요청한 기관 정보가 없습니다. 기관 등록 후 의뢰 수정 바랍니다.")