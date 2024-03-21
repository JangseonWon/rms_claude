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
    implementation(libs.bundles.jooq)
    implementation(libs.spring.gateway)
    jooqGenerator("org.postgresql:postgresql:42.6.0")

    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
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
                            SchemaMappingType().withInputSchema("public"),
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