rootProject.name = "rms"

include("gateway")
include("entity")
include("service")
include("management-service")
include("order")
include("user")
include("alis-api")
include("alis-sync")
include("api")
include("login")
include("organization-service")
include("product-service")
include("order-service")
include("authentication")


dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("reflect", "org.jetbrains.kotlin", "kotlin-reflect").withoutVersion()
            bundle("kotlin", listOf("reflect"))

            library("spring-webflux", "org.springframework.boot", "spring-boot-starter-webflux").withoutVersion()
            library("kotlin-reactor", "io.projectreactor.kotlin", "reactor-kotlin-extensions").withoutVersion()
            library("kotlin-coroutines-reactor", "org.jetbrains.kotlinx", "kotlinx-coroutines-reactor").withoutVersion()
            library("kotlin-jackson", "com.fasterxml.jackson.module", "jackson-module-kotlin").withoutVersion()
            bundle("kotlin-webflux", listOf("spring-webflux", "reflect", "kotlin-reactor", "kotlin-coroutines-reactor", "kotlin-jackson"))

            library("spring-gateway", "org.springframework.cloud", "spring-cloud-starter-gateway").withoutVersion()
            library("spring-actuator", "org.springframework.boot", "spring-boot-starter-actuator").withoutVersion()
            library("spring-hateoas", "org.springframework.hateoas", "spring-hateoas").withoutVersion()
            library("spring-kafka", "org.springframework.cloud", "spring-cloud-starter-stream-kafka").withoutVersion()

            library("spring-log4j2", "org.springframework.boot", "spring-boot-starter-log4j2").withoutVersion()
            library("spring-security", "org.springframework.boot", "spring-boot-starter-security").withoutVersion()
            bundle("spring-client", listOf("spring-log4j2", "spring-security"))

            library("bouncycastle-bcprov", "org.bouncycastle", "bcprov-jdk18on").version { require("1.77") }
            library("jjwt-api", "io.jsonwebtoken", "jjwt-api").version { require("0.12.5") }
            library("jjwt-impl", "io.jsonwebtoken", "jjwt-impl").version { require("0.12.5") }
            library("jjwt-jackson", "io.jsonwebtoken", "jjwt-jackson").version { require("0.12.5") }
            bundle("jjwt-runtime", listOf("jjwt-impl", "jjwt-jackson"))

            library("r2dbc", "org.springframework.boot", "spring-boot-starter-data-r2dbc").withoutVersion()
            library("r2dbc-postgres", "org.postgresql", "r2dbc-postgresql").version { require("1.0.0.RELEASE") }
            bundle("r2dbc-postgres", listOf("r2dbc", "r2dbc-postgres"))
            library("cassandra", "org.springframework.boot", "spring-boot-starter-data-cassandra").withoutVersion()


            library("spring-boot-test", "org.springframework.boot", "spring-boot-starter-test").withoutVersion()
            library("mockk", "io.mockk", "mockk").version { require("1.13.4") }
            library("reactor-test", "io.projectreactor", "reactor-test").withoutVersion()
            library("kotlin-test", "org.jetbrains.kotlin", "kotlin-test").withoutVersion()
            bundle("test", listOf("spring-boot-test", "mockk", "reactor-test", "kotlin-test"))


            library("spring-cloud-bom", "org.springframework.cloud", "spring-cloud-dependencies").version { require("2023.0.0") }


            library("lombok", "org.projectlombok", "lombok").version { require("1.18.26") }
            library("jackson-annotations", "com.fasterxml.jackson.core", "jackson-annotations").version { require("2.14.2") }
            library("jackson-datatype-jsr310", "com.fasterxml.jackson.datatype", "jackson-datatype-jsr310").version { require("2.14.2") }

            library("jooq", "org.jooq", "jooq").version{ require("3.19.0") }
            library("jooq-codegen", "org.jooq", "jooq-codegen").version{ require("3.19.0") }
            library("jooq-meta", "org.jooq", "jooq-meta").version{ require("3.19.0") }
            bundle("jooq", listOf("jooq", "jooq-codegen", "jooq-meta"))
        }
    }
}
