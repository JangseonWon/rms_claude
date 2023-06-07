package com.gcgenome.rms.repo

import com.gcgenome.rms.entity.SampleExtension
import com.infobip.spring.data.r2dbc.QuerydslR2dbcFragment
import org.springframework.data.querydsl.ReactiveQuerydslPredicateExecutor
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono
import java.util.*

interface ExtensionRepository : ReactiveCrudRepository<SampleExtension, SampleExtension.Companion.SampleExtensionPK>, ReactiveQuerydslPredicateExecutor<SampleExtension>, QuerydslR2dbcFragment<SampleExtension>{

    fun deleteAllBySampleId(sampleId: UUID): Mono<Void>
    fun existsBySampleId(sampleId: UUID):Mono<Boolean>
}