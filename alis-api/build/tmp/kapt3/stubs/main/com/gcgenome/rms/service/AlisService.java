package com.gcgenome.rms.service;

@org.springframework.stereotype.Service()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0017\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006H\u0016J\u000e\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u0006H\u0016J\u000e\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0006H\u0016J\u000e\u0010\f\u001a\b\u0012\u0004\u0012\u00020\r0\u0006H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2 = {"Lcom/gcgenome/rms/service/AlisService;", "", "alisRepo", "Lcom/gcgenome/rms/repository/AlisRepo;", "(Lcom/gcgenome/rms/repository/AlisRepo;)V", "getExtensions", "", "Lcom/gcgenome/rms/model/ExtensionDTO;", "getOrganizations", "Lcom/gcgenome/rms/model/OrganizationDTO;", "getSampleTypes", "Lcom/gcgenome/rms/model/LabSampleCodeDTO;", "getServices", "Lcom/gcgenome/rms/model/LabTestCodeDTO;", "alis-api"})
public class AlisService {
    @org.jetbrains.annotations.NotNull()
    private final com.gcgenome.rms.repository.AlisRepo alisRepo = null;
    
    public AlisService(@org.jetbrains.annotations.NotNull()
    com.gcgenome.rms.repository.AlisRepo alisRepo) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public java.util.List<com.gcgenome.rms.model.LabTestCodeDTO> getServices() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public java.util.List<com.gcgenome.rms.model.LabSampleCodeDTO> getSampleTypes() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public java.util.List<com.gcgenome.rms.model.OrganizationDTO> getOrganizations() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public java.util.List<com.gcgenome.rms.model.ExtensionDTO> getExtensions() {
        return null;
    }
}