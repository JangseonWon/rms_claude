plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}
dependencies {
    implementation(libs.spring.webflux)
    implementation(libs.spring.log4j2)
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")
}
dependencyManagement { imports { mavenBom(libs.spring.cloud.bom.get().toString()) } }
configurations { all { exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging") } }
