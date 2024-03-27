package com.gcgenome.rms.service

import com.fasterxml.jackson.databind.JsonNode
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class ExcelDownloadHandler {

    fun generateExcelDate(): Mono<Int> {
        val ofPattern = DateTimeFormatter.ofPattern("yyyyMMdd")
        val date = LocalDate.now().format(ofPattern).toInt()
        return Mono.just(date)
    }

    fun generateExcelFile(json: JsonNode): Mono<ByteArray> {
        return Mono.fromCallable {
            val header = json["header"]
            val body = json["body"]

            if (header == null || header.isEmpty) {
                throw IllegalArgumentException()
            }

            val wb = XSSFWorkbook()
            val sheet = wb.createSheet("Details")

            val headerRow: Row = sheet.createRow(0)
            header.forEachIndexed { index, headerValue ->
                val cell: Cell = headerRow.createCell(index)
                cell.setCellValue(headerValue.asText())
            }

            var rowNum = 1
            body.forEach { rowNode ->
                val bodyRow: Row = sheet.createRow(rowNum++)
                header.forEachIndexed { index, headerValue ->
                    val value = rowNode[headerValue.asText()]
                    if (value != null) {
                        when {
                            value.isBoolean -> bodyRow.createCell(index).setCellValue(value.asBoolean())
                            value.isInt -> bodyRow.createCell(index).setCellValue(value.asDouble())
                            else -> bodyRow.createCell(index).setCellValue(value.asText())
                        }
                    } else {
                        bodyRow.createCell(index).setCellValue("")
                    }
                }
            }

            val outputStream = ByteArrayOutputStream()
            wb.write(outputStream)
            outputStream.use {
                it.toByteArray()
            }
        }
    }
}