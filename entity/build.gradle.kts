plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa") version "1.9.22"
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
}
kotlin.jvmToolchain(21)

dependencies {
    implementation(libs.spring.webflux)
    implementation(libs.spring.log4j2)
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")
}
dependencyManagement { imports { mavenBom(libs.spring.cloud.bom.get().toString()) } }
configurations { all { exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging") } }
kotlin.jvmToolchain(21)
