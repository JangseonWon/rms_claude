package com.idrsys.ailis.rms.infrastructure.config

import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import java.nio.charset.StandardCharsets
import java.util.Locale

/**
 * 메시지 국제화(i18n) 설정
 *
 * 한국어를 기본 언어로 설정하고, 영어를 지원합니다.
 * Validation 메시지도 MessageSource를 사용하도록 설정합니다.
 */
@Configuration
class MessageConfig {

    /**
     * 메시지 소스 빈 설정
     *
     * - 기본 경로: src/main/resources/messages/
     * - 기본 언어: 한국어 (ko)
     * - 지원 언어: 한국어(ko), 영어(en)
     */
    @Bean
    fun messageSource(): MessageSource {
        return ResourceBundleMessageSource().apply {
            setBasenames("messages/messages")
            setDefaultEncoding(StandardCharsets.UTF_8.name())
            setDefaultLocale(Locale.KOREAN)
            setFallbackToSystemLocale(false)
            setCacheSeconds(3600) // 1시간 캐시
        }
    }

    /**
     * Validator 설정
     *
     * Bean Validation 메시지가 MessageSource를 사용하도록 설정
     */
    @Bean
    fun validator(messageSource: MessageSource): LocalValidatorFactoryBean {
        return LocalValidatorFactoryBean().apply {
            setValidationMessageSource(messageSource)
        }
    }
}
