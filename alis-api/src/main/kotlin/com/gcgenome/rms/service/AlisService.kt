package com.gcgenome.rms.service

import com.gcgenome.rms.model.LabSampleCodeDTO
import com.gcgenome.rms.model.LabTestCodeDTO
import com.gcgenome.rms.model.OrganizationDTO
import com.gcgenome.rms.repository.AlisRepo
import org.springframework.stereotype.Service

@Service
class AlisService(
    private val alisRepo: AlisRepo
) {
    fun getServices(): List<LabTestCodeDTO> = alisRepo.findService()
    fun getSampleTypes(): List<LabSampleCodeDTO> = alisRepo.findSampleType()
    fun getOrganizations(): List<OrganizationDTO> = alisRepo.findOrganizations()
}