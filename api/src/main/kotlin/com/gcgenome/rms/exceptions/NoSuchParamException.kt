package com.gcgenome.rms.exceptions

class NoSuchParamException(param: String) :RuntimeException("$param 입력해 주세요.")