package com.gcgenome.rms.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.gcgenome.rms.dao.ServiceDao
import com.gcgenome.rms.data.Service
import com.gcgenome.rms.data.ServiceResponse
import org.jooq.DSLContext
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux

@Component
class ServiceHandler(
    val dslContext: DSLContext,
    val webClient: WebClient
): ServiceDao {

    fun syncServices(): Flux<Service> {
        val requestBody = """
        <?xml version="1.0" encoding="utf-8"?>
        <soap12:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap12="http://www.w3.org/2003/05/soap-envelope">
            <soap12:Header>
                <AuthenticationHeader xmlns="http://tempuri.org/">
                    <UserName>LIMS</UserName>
                    <Password>LIMS</Password>
                </AuthenticationHeader>
            </soap12:Header>
            <soap12:Body>
                <GetListLabTestCodeAndSubCode xmlns="http://tempuri.org/">
                </GetListLabTestCodeAndSubCode>
            </soap12:Body>
        </soap12:Envelope>
        """.trimIndent()

        return webClient.post()
            .uri("http://gcgenome.labcenter.kr/LabGCGenomeInterfaceService.asmx")
            .header(HttpHeaders.CONTENT_TYPE, "application/soap+xml; charset=utf-8")
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(String::class.java)
            .flatMapMany  { responseBody ->
                val services = parseServices(responseBody)
                dslContext.syncServiceByResponseBody(services)
            }
    }

    private fun parseServices(responseBody: String): List<Service> {
        val jsonResponse = responseBody
            .substringAfter("<GetListLabTestCodeAndSubCodeResult>")
            .substringBefore("</GetListLabTestCodeAndSubCodeResult>")
            .replace("}{", "},{")
        val mapper = jacksonObjectMapper()
        return mapper.readValue(jsonResponse, object : TypeReference<List<ServiceResponse>>() {})
            .map { Service(it.testCode, it.testDisplayName, null) }
    }
}