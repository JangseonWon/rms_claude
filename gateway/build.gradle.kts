plugins {
    kotlin("jvm")
    id("org.springframework.boot") version "3.0.3"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.jetbrains.kotlin.plugin.spring") version "1.8.22"
    id("com.google.cloud.tools.jib") version "3.3.2"
}
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("org.postgresql:r2dbc-postgresql:1.0.0.RELEASE")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")

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

configurations { all { exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging") } }
dependencyManagement { imports { mavenBom(libs.spring.cloud.bom.get().toString()) } }
kotlin.jvmToolchain(17)
//tasks.processResources { if(project.gradle.startParameter.taskNames.contains("jib")) exclude("application.yml") }
