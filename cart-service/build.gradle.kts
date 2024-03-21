import nu.studer.gradle.jooq.JooqEdition
import org.jooq.meta.jaxb.Logging
import org.jooq.meta.jaxb.SchemaMappingType

plugins {
    kotlin("jvm")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("org.jetbrains.kotlin.plugin.spring")
    id("com.google.cloud.tools.jib")
    id("nu.studer.jooq") 
}
dependencies {
    implementation(libs.bundles.spring.client)
    implementation(libs.bundles.kotlin.webflux)
    implementation(libs.bundles.r2dbc.postgres)
    implementation("org.jooq:jooq:3.18.2")
    implementation("org.jooq:jooq-codegen:3.18.2")
    implementation("org.jooq:jooq-meta:3.18.2")
    jooqGenerator("org.postgresql:postgresql:42.6.0")
}
configurations { all { exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging") } }
dependencyManagement { imports { mavenBom(libs.spring.cloud.bom.get().toString()) } }

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