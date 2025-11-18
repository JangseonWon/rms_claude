import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.spring") version "1.9.23"
    id("nu.studer.jooq") version "9.0"
}

group = "com.idrsys.ailis"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot WebFlux
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // R2DBC
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("io.r2dbc:r2dbc-postgresql")

    // jOOQ
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.jooq:jooq:3.19.15")
    implementation("org.jooq:jooq-kotlin:3.19.15")

    // Validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.openapitools:jackson-databind-nullable:0.2.6")

    // Spring Cloud Config (optional)
    // implementation("org.springframework.cloud:spring-cloud-starter-config")

    // Development Tools
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.mockk:mockk:1.13.14")
    testImplementation("org.testcontainers:postgresql:1.20.4")
    testImplementation("org.testcontainers:r2dbc:1.20.4")

    // PostgreSQL JDBC (for jOOQ code generation)
    jooqGenerator("org.postgresql:postgresql:42.7.4")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "21"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// jOOQ Configuration
jooq {
    version.set("3.19.15")

    configurations {
        create("main") {
            generateSchemaSourceOnCompilation.set(false)

            jooqConfiguration.apply {
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = "jdbc:postgresql://localhost:5432/rms"
                    user = "rms_user"
                    password = "rms_password"
                }
                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                        includes = ".*"
                        excludes = "flyway_schema_history"
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = true
                        isFluentSetters = true
                        isKotlinNotNullPojoAttributes = true
                        isKotlinNotNullRecordAttributes = true
                    }
                    target.apply {
                        packageName = "com.idrsys.ailis.rms.adapter.persistence.jooq"
                        directory = "src/main/kotlin"
                    }
                }
            }
        }
    }
}
