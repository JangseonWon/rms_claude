package com.gcgenome.rms.repo

import com.gcgenome.rms.entity.User
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface UserRepository : R2dbcRepository<User, String>