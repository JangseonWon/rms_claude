package com.gcgenome.rms.alis

import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.*
import com.gcgenome.rms.history.UserHistoryHandler
import org.jooq.DSLContext
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class AlisHandler(
    val dslContext: DSLContext,
    val encoder: BCryptPasswordEncoder,
    val userHistoryHandler: UserHistoryHandler
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
                        checkDeferByAlis(dsl(), alisOrganization).flatMap { isDifferent ->
                            if (isDifferent) {
                                upsertUsers(alisOrganization, pwd)
                                    .flatMap { userHistoryHandler.logUserChanges(trx.dsl(), userId, it) }
                                    .flatMap { insertOrganization(alisOrganization) }
                            } else {
                                Mono.empty()
                            }
                        }
                    }
                }
            }
            .then(dslContext.selectUsersWithPage(query))
    }

    fun checkDeferByAlis(dsl: DSLContext, alisUser: AlisOrganization): Mono<Boolean> {
        return Mono.from(
            dsl.selectUserById(alisUser.compCode).map { user ->
                val isDifferent = (user.name != alisUser.compName) ||
                        (user.branchSerial != alisUser.compMngBeginNo) ||
                        (user.branchName != alisUser.compMngName)
                isDifferent
            }.switchIfEmpty(Mono.just(true))
        )
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