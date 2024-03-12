package com.gcgenome.rms.exception

import org.jooq.exception.IntegrityConstraintViolationException

class DatabaseConstraintViolationException(): IntegrityConstraintViolationException("중복된 ID가 있습니다.")