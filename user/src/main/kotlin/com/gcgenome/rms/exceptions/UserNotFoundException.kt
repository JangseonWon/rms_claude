package com.gcgenome.rms.exceptions

import java.util.*

class UserNotFoundException(): RuntimeException("아이디 또는 비밀번호를 잘못 입력했습니다.")