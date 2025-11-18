package com.idrsys.ailis.rms

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * RMS Service 애플리케이션
 *
 * - Spring Boot 3.5 + Kotlin
 * - WebFlux (Reactive)
 * - R2DBC (Reactive Database)
 * - Clean Architecture
 */
@SpringBootApplication
class RmsServiceApplication

fun main(args: Array<String>) {
    runApplication<RmsServiceApplication>(*args)
}
