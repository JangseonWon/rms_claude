package com.gcgenome.rms.alis

import com.gcgenome.rms.dao.ExtensionDao
import com.gcgenome.rms.dao.SampleTypeDao
import com.gcgenome.rms.dao.ServiceDao
import com.gcgenome.rms.dao.UserDao
import com.gcgenome.rms.data.*
import com.gcgenome.rms.tables.pojos.SampleType
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class AlisHandler(
    val dslContext: DSLContext,
    val encoder: BCryptPasswordEncoder
):ServiceDao, SampleTypeDao,ExtensionDao, UserDao {
    private val webClient = WebClient.builder().baseUrl("https://rms-test.gcgenome.com").build()

    fun updateUsers(query: Query): Mono<Page<UserDTO>> {
        return webClient.get()
            .uri("/w-api/alis-api/organizations")
            .retrieve()
            .bodyToFlux(AlisOrganization::class.java)
            .flatMap { alisOrganization ->
                dslContext.transactionPublisher { trx ->
                    trx.dsl().run {
                        val pwd = encoder.encode("GCGenome00!")
                        upsertUsers(alisOrganization, pwd)
                    }
                }
            }
            .then(dslContext.selectUsersWithPage(query))
    }

    fun updateServices(query: Query): Mono<Page<ServiceDTO>> {
        return webClient.get()
            .uri("/w-api/alis-api/services")
            .retrieve()
            .bodyToFlux(AlisService::class.java)
            .flatMap { alisService ->
                dslContext.transactionPublisher { trx ->
                    trx.dsl().upsertService(alisService)
                }
            }
            .then(dslContext.selectServicesWithPage(query))
    }

    fun updateSampleTypes(query: Query): Mono<Page<SampleTypeDTO>> {
        return webClient.get()
            .uri("/w-api/alis-api/sample-types")
            .retrieve()
            .bodyToFlux(AlisSampleType::class.java)
            .flatMap { alisSampleType ->
                dslContext.transactionPublisher { trx ->
                    trx.dsl().upsertSampleType(alisSampleType)
                }
            }
            .then(dslContext.selectSampleTypesWithPage(query))
    }

    fun updateExtensions(query: Query): Mono<Page<ExtensionDTO>> {
        return webClient.get()
            .uri("/w-api/alis-api/extensions")
            .retrieve()
            .bodyToFlux(AlisExtension::class.java)
            .flatMap { alisExtension ->
                dslContext.transactionPublisher { trx ->
                    trx.dsl().upsertExtension(alisExtension)
                }
            }
            .then(dslContext.selectExtensionsWithPage(query))
    }
}