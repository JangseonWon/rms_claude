package com.gcgenome.lims.subscriber

import com.fasterxml.jackson.databind.ObjectMapper
import com.gcgenome.lims.data.ReportMessage
import com.gcgenome.lims.data.WorkflowMessage
import com.gcgenome.lims.exception.InvalidWorkflowException
import com.gcgenome.lims.exception.NotFoundBarcodeException
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

    @Bean("reportSubscribe")
    fun reportSubscribe(): Consumer<String> {
        return Consumer { json ->
            val message = om.readValue(json, ReportMessage::class.java)
            handler.saveReport(message)
                .switchIfEmpty(Mono.fromRunnable { logger.info("[REPORT] empty - sample: ${message.sample}, service: ${message.service}") })
                .subscribe(
                    {report -> logger.info("[REPORT] Report saved reportId: ${report.id}")},
                    {error ->
                        when (error) {
                            is NotFoundBarcodeException -> logger.warn("[REPORT] ${error.message}")
                            else -> logger.error("[REPORT] Error saving report", error)
                        }
                    }
                )
        }
    }
    @Bean("workflowSubscribe")
    fun workflowSubscribe(): Consumer<String> {
        return Consumer { json ->
            val message = om.readValue(json, WorkflowMessage::class.java)
            handler.updateRequest(message)
                .switchIfEmpty(Mono.fromRunnable { logger.info("[WORKFLOW] empty - sampleBarcode: ${message.request.samples[0].id}, service: ${message.request.service.id}") })
                .subscribe(
                    {request -> logger.info("[WORKFLOW] Successfully updated request barcode: ${message.request.samples[0].id}, sampleID: ${request.sampleId}, status: ${request.status}")},
                    {error ->
                        when (error) {
                            is InvalidWorkflowException -> logger.warn("[WORKFLOW] ${error.message}")
                            is NotFoundBarcodeException -> logger.warn("[WORKFLOW] ${error.message}")
                            else -> logger.error("[WORKFLOW] Failed to update request", error)
                        }
                    }
                )
        }
    }
}