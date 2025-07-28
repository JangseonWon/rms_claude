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
                .doOnError {error -> logger.error("[REPORT] Error saving report: barcode=${message.sample} / serviceId=${message.service}", error) }
                .subscribe {report -> logger.info("[REPORT] Report saved: barcode=${message.sample} / serviceId=${report.serviceId} / reportId=${report.id}")}


        }
    }
    @Bean("workflowSubscribe")
    fun workflowSubscribe(): Consumer<String> {
        return Consumer { json ->
            val message = om.readValue(json, WorkflowMessage::class.java)
            handler.updateRequest(message)
                .doOnError {error -> logger.error("[REPORT] Error saving report: barcode=${message.request.samples[0]} / serviceId=${message.request.service}", error) }
                .subscribe {request -> logger.info("[WORKFLOW] Successfully updated request barcode: ${message.request.samples[0].id} / sampleID: ${request.sampleId} / status: ${request.status}")}

        }
    }
}