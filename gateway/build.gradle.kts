plugins {
    kotlin("jvm")
    id("org.springframework.boot") version "3.0.3"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.jetbrains.kotlin.plugin.spring") version "1.8.22"
}
dependencies {
    implementation(libs.bundles.spring.client)
    implementation(libs.bundles.kotlin.webflux)
    implementation(libs.bundles.r2dbc.postgres)
    implementation(libs.bundles.r2dbc.querydsl)
    implementation(libs.bundles.jjwt.runtime)
    implementation(libs.spring.gateway)
}
configurations { all { exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging") } }
dependencyManagement { imports { mavenBom(libs.spring.cloud.bom.get().toString()) } }
kotlin.jvmToolchain(17)
tasks.processResources {
    if(project.gradle.startParameter.taskNames.contains("build")) exclude("application.yml")
}
springBoot.mainClass.set("com.gcgenome.rms.ApplicationKt")