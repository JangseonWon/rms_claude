plugins {
    kotlin("jvm") version "1.8.10" apply false
    kotlin("kapt") version "1.8.10" apply false
}
group = "com.gcgenome"
version = "1.0"

subprojects {
    repositories {
        mavenCentral()
        mavenLocal()
        maven{
            url= uri("http://gemini/api/packages/LIMS/maven")
            isAllowInsecureProtocol=true
        }
    }
}
