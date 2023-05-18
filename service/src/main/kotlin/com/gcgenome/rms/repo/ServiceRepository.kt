package com.gcgenome.rms.repo

import com.gcgenome.rms.entity.Service
import com.infobip.spring.data.r2dbc.QuerydslR2dbcFragment
import org.springframework.data.querydsl.ReactiveQuerydslPredicateExecutor
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface ServiceRepository : ReactiveCrudRepository<Service, String>, ReactiveQuerydslPredicateExecutor<Service>, QuerydslR2dbcFragment<Service>