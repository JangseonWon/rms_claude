package com.gcgenome.rms.alis

import com.gcgenome.rms.dao.ExtensionDao
import com.gcgenome.rms.dao.SampleTypeDao
import com.gcgenome.rms.dao.ServiceDao
import com.gcgenome.rms.data.*
import com.gcgenome.rms.tables.pojos.SampleType
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class AlisHandler(
    val dslContext: DSLContext,
):ServiceDao, SampleTypeDao,ExtensionDao {
    private val webClient = WebClient.builder().baseUrl("https://rms-test.gcgenome.com").build()
    fun updateServices(query: Query): Mono<Page<ServiceDTO>> {
        return webClient.get()
            .uri("/w-api/alis-api/services")
            .retrieve()
            .bodyToFlux(AlisService::class.java)
            .flatMap { alisService -> dslContext.run { upsertService(alisService) } }
            .then(dslContext.selectServicesWithPage(query))
    }

    fun updateSampleTypes(query: Query): Mono<Page<SampleType>> {
        return webClient.get()
            .uri("/w-api/alis-api/sample-types")
            .retrieve()
            .bodyToFlux(AlisSampleType::class.java)
            .flatMap { alisService -> dslContext.run { upsertSampleType(alisService) } }
            .then(dslContext.selectSampleTypesWithPage(query))
    }

    fun updateExtensions(query: Query): Mono<Page<ExtensionDTO>> {
        return webClient.get()
            .uri("/w-api/alis-api/extensions")
            .retrieve()
            .bodyToFlux(AlisExtension::class.java)
            .flatMap { alisExtension -> dslContext.run { upsertExtension(alisExtension)}}
            .then(dslContext.selectExtensionsWithPage(query))
    }
}