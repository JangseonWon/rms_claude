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
    implementation(libs.spring.log4j2)
    implementation(libs.spring.validation)
    implementation(libs.bundles.kotlin.webflux)
    implementation(libs.bundles.r2dbc.postgres)
    implementation(libs.bundles.jooq)
    implementation(libs.spring.security)
    jooqGenerator("org.postgresql:postgresql:42.6.0")
    implementation("org.openapitools:jackson-databind-nullable:0.2.6")
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
tasks.processResources {
    doFirst {
        if (gradle.taskGraph.allTasks.any { it.name == "jib" }) { exclude("application.yml") }
    }
}
jooq {
    version.set("3.18.2")
    edition.set(JooqEdition.OSS)
    configurations{
        create("main") {
            jooqConfiguration.apply {
                logging = Logging.WARN
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = System.getenv("POSTGRES_URL")
                    user = System.getenv("POSTGRES_USERNAME")
                    password = System.getenv("POSTGRES_PASSWORD")
                }
                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        schemata = listOf(
                            SchemaMappingType().withInputSchema(System.getenv("POSTGRES_SCHEMA")),
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