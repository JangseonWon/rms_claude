package com.idrsys.ailis.rms.infrastructure.config

import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator

/**
 * R2DBC 설정
 *
 * - Reactive 데이터베이스 접근 설정
 * - Auditing 활성화 (생성일시, 수정일시 자동 관리)
 */
@Configuration
@EnableR2dbcAuditing
class R2dbcConfig {

    /**
     * 데이터베이스 초기화 (개발 환경용)
     *
     * 주의: 운영 환경에서는 별도의 마이그레이션 도구(Flyway, Liquibase) 사용 권장
     */
    @Bean
    fun initializer(connectionFactory: ConnectionFactory): ConnectionFactoryInitializer {
        val initializer = ConnectionFactoryInitializer()
        initializer.setConnectionFactory(connectionFactory)

        // schema.sql이 존재하면 실행 (선택사항)
        // initializer.setDatabasePopulator(
        //     ResourceDatabasePopulator(ClassPathResource("schema.sql"))
        // )

        return initializer
    }
}
