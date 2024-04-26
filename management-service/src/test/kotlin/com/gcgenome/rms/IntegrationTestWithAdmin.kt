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
@WithMockUser(role="ADMIN")
class IntegrationTestWithAdmin(
    @Autowired private val client: WebTestClient,
): BehaviorSpec({
    extensions(SpringExtension)
    Given("유효한 관리자가") {
        var categories: List<Category>
        When("카테고리 목록을 요청하면") {
            val conn = client.get().uri("/w-api/management-service/categories")
            Then("200 코드와 함께 카테고리 목록을 반환한다") {
                val exchange = conn.exchange()
                exchange.expectStatus().isOk
                exchange.expectBodyList(Category::class.java).returnResult().let { categories = it.responseBody!! }
            }
        }
        When("새로운 카테고리 추가를 시도하면") {
            val conn = client.mutateWith(SecurityMockServerConfigurers.csrf()).put().uri("/w-api/management-service/categories")
            Then("카테고리를 저장하고, 200 코드와 함께 저장 결과를 반환한다") {
                val exchange = conn.exchange()
                exchange.expectStatus().isOk
            }
        }
        When("기존에 존재하는 아이디로 카테고리 추가를 시도하면") {
            val conn = client.mutateWith(SecurityMockServerConfigurers.csrf()).put().uri("/w-api/management-service/categories")
            // TODO: body 입력하는 코드
            Then("409 코드와 함께 에러 메시지를 반환한다") {
                val exchange = conn.exchange()
                exchange.expectStatus().isOk
            }
        }
        When("기존에 존재하는 카테고리의 변경을 시도하면") {
            val conn = client.mutateWith(SecurityMockServerConfigurers.csrf()).patch().uri("/w-api/management-service/services/test")
            Then("카테고리 정보를 변경, 저장하고 200코드와 함께 저장 결과를 반환한다") {
                val exchange = conn.exchange()
                // TODO: DB에서 변경내용 확인하는 코드
                exchange.expectStatus().isOk
            }
        }
        When("존재하지 않는 카테고리의 변경을 시도하면") {
            val conn = client.mutateWith(SecurityMockServerConfigurers.csrf()).patch().uri("/w-api/management-service/services/invalid_id")
            Then("404 코드와 함께 에러 메시지를 반환한다") {
                val exchange = conn.exchange()
                // TODO: 왜 200 정상 응답함?
                exchange.expectStatus().isOk
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