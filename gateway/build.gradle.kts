import nu.studer.gradle.jooq.JooqEdition
import org.jooq.meta.jaxb.Logging
import org.jooq.meta.jaxb.SchemaMappingType

plugins {
    kotlin("jvm")
    id("org.springframework.boot") version "3.1.1"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.jetbrains.kotlin.plugin.spring") version "1.8.22"
    id("com.google.cloud.tools.jib") version "3.3.2"
    id("nu.studer.jooq") version "8.1"
}
dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    implementation("org.springframework.cloud:spring-cloud-starter-zookeeper-discovery")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.postgresql:r2dbc-postgresql:1.0.1.RELEASE")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")
    implementation(libs.spring.actuator)

    implementation("org.jooq:jooq:3.18.2")
    implementation("org.jooq:jooq-codegen:3.18.2")
    implementation("org.jooq:jooq-meta:3.18.2")
    jooqGenerator("org.postgresql:postgresql:42.6.0")
}

jib {
    from {
        image = "eclipse-temurin:17.0.7_7-jre-jammy"
    }
    container {
        environment = mapOf(
            "LANG" to "C.UTF-8",
            "TZ" to "Asia/Seoul",
        )
    }
}

dependencyManagement { imports { mavenBom(libs.spring.cloud.bom.get().toString()) } }
kotlin.jvmToolchain(17)
tasks.processResources { if(project.gradle.startParameter.taskNames.contains("jib")) exclude("application.yml") }

jooq {
    version.set("3.18.2")
    edition.set(JooqEdition.OSS)
    configurations{
        create("main") {
            jooqConfiguration.apply {
                logging = Logging.WARN
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = "jdbc:postgresql://172.19.216.212:5432/rms"
                    user = "postgres"
                    password = "snubi1004"
                }
                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        schemata = listOf(
                            SchemaMappingType().withInputSchema("rms")
                        )
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isPojos = true
                        isJpaAnnotations = false
                        isFluentSetters = false
                    }
                    target.apply {
                        packageName = "com.gcgenome.rms"
                        directory = "build/generated/jooq"
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }
}
