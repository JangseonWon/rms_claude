package com.gcgenome.rms.repo

import com.gcgenome.rms.entity.OrganizationService
import com.infobip.spring.data.r2dbc.QuerydslR2dbcFragment
import org.springframework.data.querydsl.ReactiveQuerydslPredicateExecutor
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface OrganizationService : ReactiveCrudRepository<OrganizationService, String>, ReactiveQuerydslPredicateExecutor<OrganizationService>, QuerydslR2dbcFragment<OrganizationService>
