plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("org.springframework.boot") version "3.0.3"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.jetbrains.kotlin.plugin.spring") version "1.8.10"
}
java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17
dependencies {
    implementation(libs.bundles.kotlin)
    implementation(libs.kotlin.reactor)
    implementation(libs.kotlin.coroutines.reactor)
    implementation(libs.kotlin.jackson)
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")
    implementation(libs.webflux)
    implementation(libs.spring.log4j2)
}
configurations { all { exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging") } }
/*dependencyManagement { imports { mavenBom(libs.spring.cloud.bom.get().toString()) } }
kapt {
    includeCompileClasspath = false
}*/
tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
    getByName<Jar>("jar") {
        enabled = false
    }
}
