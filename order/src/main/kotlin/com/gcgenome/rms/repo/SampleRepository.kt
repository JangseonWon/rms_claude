package com.gcgenome.rms.repo

import com.gcgenome.rms.entity.Sample
import com.infobip.spring.data.r2dbc.QuerydslR2dbcFragment
import org.springframework.data.querydsl.ReactiveQuerydslPredicateExecutor
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono
import java.util.*

interface SampleRepository : ReactiveCrudRepository<Sample, UUID>, ReactiveQuerydslPredicateExecutor<Sample>, QuerydslR2dbcFragment<Sample> {

    override fun existsById(sampleId: UUID): Mono<Boolean>
    fun countByItemId(sampleId: UUID): Mono<Long>
}