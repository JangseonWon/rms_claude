package com.gcgenome.rms.cotroller;

@org.springframework.web.bind.annotation.RestController()
@org.springframework.web.bind.annotation.RequestMapping(value = {"/w-api/alis-api"})
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0017\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0014\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006H\u0017J\u0014\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\u00070\u0006H\u0017J\u0014\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u00070\u0006H\u0017J\u0014\u0010\r\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\u00070\u0006H\u0017R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2 = {"Lcom/gcgenome/rms/cotroller/AlisController;", "", "alisService", "Lcom/gcgenome/rms/service/AlisService;", "(Lcom/gcgenome/rms/service/AlisService;)V", "extensions", "Lorg/springframework/http/ResponseEntity;", "", "Lcom/gcgenome/rms/model/ExtensionDTO;", "organizations", "Lcom/gcgenome/rms/model/OrganizationDTO;", "sampleTypes", "Lcom/gcgenome/rms/model/LabSampleCodeDTO;", "services", "Lcom/gcgenome/rms/model/LabTestCodeDTO;", "alis-api"})
public class AlisController {
    @org.jetbrains.annotations.NotNull()
    private final com.gcgenome.rms.service.AlisService alisService = null;
    
    public AlisController(@org.jetbrains.annotations.NotNull()
    com.gcgenome.rms.service.AlisService alisService) {
        super();
    }
    
    @org.springframework.web.bind.annotation.GetMapping(value = {"/services"})
    @org.jetbrains.annotations.NotNull()
    public org.springframework.http.ResponseEntity<java.util.List<com.gcgenome.rms.model.LabTestCodeDTO>> services() {
        return null;
    }
    
    @org.springframework.web.bind.annotation.GetMapping(value = {"/sample-types"})
    @org.jetbrains.annotations.NotNull()
    public org.springframework.http.ResponseEntity<java.util.List<com.gcgenome.rms.model.LabSampleCodeDTO>> sampleTypes() {
        return null;
    }
    
    @org.springframework.web.bind.annotation.GetMapping(value = {"/organizations"})
    @org.jetbrains.annotations.NotNull()
    public org.springframework.http.ResponseEntity<java.util.List<com.gcgenome.rms.model.OrganizationDTO>> organizations() {
        return null;
    }
    
    @org.springframework.web.bind.annotation.GetMapping(value = {"/extensions"})
    @org.jetbrains.annotations.NotNull()
    public org.springframework.http.ResponseEntity<java.util.List<com.gcgenome.rms.model.ExtensionDTO>> extensions() {
        return null;
    }
}