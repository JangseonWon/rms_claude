package com.gcgenome.lims.subscriber

import com.fasterxml.jackson.databind.ObjectMapper
import com.gcgenome.lims.data.Message
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Mono
import java.util.function.Consumer

@Configuration
class Subscriber(
    val om: ObjectMapper,
    val handler: Handler
) {
    private val logger: Logger = LoggerFactory.getLogger(Subscriber::class.java)

    @Bean("subscribe")
    fun subscribe(): Consumer<String> {
        return Consumer { json ->
            val message = om.readValue(json, Message::class.java)
            handler.saveReport(message)
                .switchIfEmpty(Mono.fromRunnable { logger.info("[empty]: sample: ${message.sample}, service: ${message.service}") })
                .subscribe(
                    {report -> logger.info("[Report saved] reportId: ${report.id}")},
                    {error -> logger.error("[Error saving report]", error)}
                )
        }
    }
}