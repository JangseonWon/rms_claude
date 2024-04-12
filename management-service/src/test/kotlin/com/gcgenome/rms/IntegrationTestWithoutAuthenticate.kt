package com.gcgenome.rms

import com.gcgenome.rms.tables.pojos.Category
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import org.junit.jupiter.api.AfterAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.MountableFile


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class IntegrationTestWithoutAuthenticate(
    @Autowired private val client: WebTestClient,
): BehaviorSpec({
    extensions(SpringExtension)
    Given("사용자 인증 정보 없이") {
        var categories: List<Category>
        When("카테고리 목록을 요청하면") {
            val conn = client.get().uri("/w-api/management-service/categories")
            Then("401 코드와 함께 에러 메시지를 반환한다") {
                val exchange = conn.exchange()
                exchange.expectStatus().isUnauthorized
            }
        }
        When("새로운 카테고리 추가를 시도하면") {
            val conn = client.mutateWith(SecurityMockServerConfigurers.csrf()).put().uri("/w-api/management-service/categories")
            Then("401 코드와 함께 에러 메시지를 반환한다") {
                val exchange = conn.exchange()
                exchange.expectStatus().isUnauthorized
            }
        }
        When("기존에 존재하는 카테고리의 변경을 시도하면") {
            val conn = client.mutateWith(SecurityMockServerConfigurers.csrf()).patch().uri("/w-api/management-service/services/test")
            Then("401 코드와 함께 에러 메시지를 반환한다") {
                val exchange = conn.exchange()
                // TODO: DB에서 변경이 없음을 확인하는 코드
                exchange.expectStatus().isUnauthorized
            }
        }
    }
}) {
    companion object {
        @JvmStatic
        private val postgres = PostgreSQLContainer("postgres:latest")
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("password")
            .withCopyFileToContainer(MountableFile.forClasspathResource("schema.sql"), "/docker-entrypoint-initdb.d/init.sql")
        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            postgres.start()
            registry.add("spring.r2dbc.url") { "r2dbc:postgresql://${postgres.host}:${postgres.firstMappedPort}/${postgres.databaseName}" }
            registry.add("spring.r2dbc.username") { postgres.username }
            registry.add("spring.r2dbc.password") { postgres.password }
        }
        @JvmStatic
        @AfterAll
        fun shutdown() = postgres.stop()
    }
}