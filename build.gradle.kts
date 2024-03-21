plugins {
    id("java")
    kotlin("jvm") version "1.9.21" apply false
    kotlin("kapt") version "1.9.21" apply false
    kotlin("plugin.spring") version "1.9.21" apply false
}
subprojects {
    repositories {
        maven("https://gitea.apps.lims.gcgenome.com/api/packages/LIMS/maven")
        mavenCentral()
    }
    group = "com.gcgenome"
    version = "1.0"
}
