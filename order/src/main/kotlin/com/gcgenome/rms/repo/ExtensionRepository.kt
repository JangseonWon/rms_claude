package com.gcgenome.rms.repo

import com.gcgenome.rms.entity.SampleExtension
import com.infobip.spring.data.r2dbc.QuerydslR2dbcFragment
import org.springframework.data.querydsl.ReactiveQuerydslPredicateExecutor
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import java.util.*

interface ExtensionRepository : ReactiveCrudRepository<SampleExtension, SampleExtension.Companion.SampleExtensionPK>, ReactiveQuerydslPredicateExecutor<SampleExtension>, QuerydslR2dbcFragment<SampleExtension>