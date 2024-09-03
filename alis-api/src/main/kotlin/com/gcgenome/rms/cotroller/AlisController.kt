package com.gcgenome.rms.cotroller

import com.gcgenome.rms.model.ExtensionDTO
import com.gcgenome.rms.model.LabSampleCodeDTO
import com.gcgenome.rms.model.LabTestCodeDTO
import com.gcgenome.rms.model.OrganizationDTO
import com.gcgenome.rms.service.AlisService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/w-api/alis-api")
class AlisController(
    private val alisService: AlisService
) {
    @GetMapping("/services")
    fun services(): ResponseEntity<List<LabTestCodeDTO>> {
        return ResponseEntity.ok(alisService.getServices())
    }
    @GetMapping("/sample-types")
    fun sampleTypes(): ResponseEntity<List<LabSampleCodeDTO>> {
        return ResponseEntity.ok(alisService.getSampleTypes())
    }
    @GetMapping("/organizations")
    fun organizations(): ResponseEntity<List<OrganizationDTO>> {
        return ResponseEntity.ok(alisService.getOrganizations())
    }
    @GetMapping("/extensions")
    fun extensions(): ResponseEntity<List<ExtensionDTO>> {
        return ResponseEntity.ok(alisService.getExtensions())
    }
}