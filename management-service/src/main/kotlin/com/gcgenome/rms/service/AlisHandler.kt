package com.gcgenome.rms.service

import com.gcgenome.rms.dao.ExtensionDao
import com.gcgenome.rms.dao.SampleTypeDao
import com.gcgenome.rms.dao.ServiceDao
import com.gcgenome.rms.data.*
import com.gcgenome.rms.tables.pojos.Extension
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class AlisHandler(
    val dslContext: DSLContext,
    private val serviceHandler: ServiceHandler
):ServiceDao, SampleTypeDao,ExtensionDao {
    private val webClient = WebClient.builder().baseUrl("https://rms-test.gcgenome.com").build()
    fun updateServices(): Mono<Page<ServiceCategory>> {
        return webClient.get()
            .uri("/w-api/alis-api/services")
            .retrieve()
            .bodyToFlux(AlisService::class.java)
            .flatMap { alisService -> dslContext.run { upsertService(alisService) } }
            .then(serviceHandler.selectServiceCategory(Query(page = 1,size = 10)))
    }

    fun updateSampleTypes(): Mono<Page<ServiceCategory>> {
        return webClient.get()
            .uri("/w-api/alis-api/sample-types")
            .retrieve()
            .bodyToFlux(AlisSampleType::class.java)
            .flatMap { alisService -> dslContext.run { upsertSampleType(alisService) } }
            .then(serviceHandler.selectServiceCategory(Query(page = 1,size = 10)))
    }

    fun updateExtensions(): Mono<Pair<Int, List<Extension>>> {
        return webClient.get()
            .uri("/w-api/alis-api/extensions")
            .retrieve()
            .bodyToFlux(AlisExtension::class.java)
            .flatMap { alisExtension -> dslContext.run { upsertExtension(alisExtension)}}
            .then(dslContext.selectExtensionsWithTotalPage(Query(page = 1, size = 10)))
    }
}