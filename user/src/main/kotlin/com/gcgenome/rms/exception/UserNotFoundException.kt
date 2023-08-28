package com.gcgenome.rms.exception

class UserNotFoundException(userId: String): RuntimeException("요청한 사용자 ${userId}를 찾을 수 없습니다.")