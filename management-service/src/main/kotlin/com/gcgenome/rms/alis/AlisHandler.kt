package com.gcgenome.rms.alis

import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.*
import org.jooq.DSLContext
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class AlisHandler(
    val dslContext: DSLContext,
    val encoder: BCryptPasswordEncoder
):ServiceDao, SampleTypeDao,ExtensionDao, UserDao, OrganizationDao {
    private val webClient = WebClient.builder().baseUrl("https://rms-test.gcgenome.com").build()

    fun updateUsers(userId: String, query: Query): Mono<Page<UserDTO>> {
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
            .then(dslContext.selectUsersWithPage(query, "ADMIN"))
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