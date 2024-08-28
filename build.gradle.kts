plugins {
    id("java")
    kotlin("jvm") version "1.9.23" apply false
    kotlin("kapt") version "1.9.23" apply false
    kotlin("plugin.spring") version "1.9.23" apply false
    kotlin("plugin.jpa") version "1.9.23" apply false
    id("org.springframework.boot") version "3.2.3" apply false
    id("io.spring.dependency-management") version "1.1.4" apply false
    id("com.google.cloud.tools.jib") version "3.4.1" apply false
    id("nu.studer.jooq") version "8.1" apply false
}
subprojects {
    repositories {
        maven("https://gitea.apps.lims.gcgenome.com/api/packages/LIMS/maven")
        mavenCentral()
    }
    group = "com.gcgenome"
    version = "1.0"
}
