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
    implementation(libs.bundles.spring.client)
    implementation(libs.bundles.kotlin.webflux)
    implementation(libs.bundles.r2dbc.postgres)
    implementation("org.jooq:jooq:3.18.2")
    implementation("org.jooq:jooq-codegen:3.18.2")
    implementation("org.jooq:jooq-meta:3.18.2")
    jooqGenerator("org.postgresql:postgresql:42.6.0")
    implementation("org.apache.poi:poi:5.1.0")
    implementation("org.apache.poi:poi-ooxml:5.1.0")
}
jib {
    from { image = "eclipse-temurin:17.0.7_7-jre-jammy" }
    container { environment = mapOf(
        "LANG" to "C.UTF-8",
        "TZ" to "Asia/Seoul",
    )}
}

configurations { all { exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging") } }
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
                    url = "jdbc:postgresql://${System.getenv("POSTGRES_URL")}/report_service"
                    user = System.getenv("POSTGRES_USERNAME")
                    password = System.getenv("POSTGRES_PASSWORD")
                }
                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        schemata = listOf(
                            SchemaMappingType().withInputSchema("rms_dev"),
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