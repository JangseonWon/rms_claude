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
    implementation(libs.bundles.kotlin.webflux)
    implementation(libs.bundles.spring.client)
    implementation(libs.bundles.r2dbc.postgres)
    implementation(libs.spring.gateway)
    implementation(libs.spring.actuator)
    implementation(libs.bundles.jooq)
    implementation("com.jcraft:jsch:0.1.55")
    implementation("software.amazon.awssdk:s3:2.20.118")
    implementation("software.amazon.awssdk:netty-nio-client:2.20.117")
    jooqGenerator("org.postgresql:postgresql:42.6.0")

}
jib {
    from { image = "eclipse-temurin:17.0.7_7-jre-jammy" }
    to {
        image = "image-registry.apps.gcgenome.com/rms-test/rms-api"
        tags = setOf("latest")
        auth {
            username = System.getenv("REGISTRY_USERNAME")
            password = System.getenv("REGISTRY_PASSWORD")
        }
    }
    container { environment = mapOf(
        "LANG" to "C.UTF-8",
        "TZ" to "Asia/Seoul",
    )
    }
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
                    url = "jdbc:postgresql://172.19.208.223:5432/rms"
                    user = System.getenv("POSTGRES_USERNAME")
                    password = System.getenv("POSTGRES_PASSWORD")
                }
                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        schemata = listOf(
                            SchemaMappingType().withInputSchema("public")
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