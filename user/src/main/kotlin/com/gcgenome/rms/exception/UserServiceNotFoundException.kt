package com.gcgenome.rms.exception

class UserServiceNotFoundException(userId: String, itemId: String): RuntimeException("요청한 ID: ${userId}, 서비스: ${itemId}를 찾을 수 없습니다.")