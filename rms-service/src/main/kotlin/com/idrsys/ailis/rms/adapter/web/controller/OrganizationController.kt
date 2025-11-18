package com.idrsys.ailis.rms.adapter.web.controller

import com.idrsys.ailis.rms.application.dto.request.CreateOrganizationCommand
import com.idrsys.ailis.rms.application.dto.request.PatchOrganizationCommand
import com.idrsys.ailis.rms.application.dto.request.UpdateOrganizationCommand
import com.idrsys.ailis.rms.application.dto.response.CreatedResponse
import com.idrsys.ailis.rms.application.dto.response.DeletedResponse
import com.idrsys.ailis.rms.application.dto.response.OrganizationListResponse
import com.idrsys.ailis.rms.application.dto.response.OrganizationResponse
import com.idrsys.ailis.rms.application.usecase.OrganizationUseCase
import jakarta.validation.Valid
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

/**
 * 기관 관리 REST Controller
 */
@RestController
@RequestMapping("/api/organizations")
class OrganizationController(
    private val organizationUseCase: OrganizationUseCase
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createOrganization(
        @Valid @RequestBody command: CreateOrganizationCommand,
        @RequestHeader("X-User-Id", required = false, defaultValue = "system") userId: String
    ): CreatedResponse {
        val response = organizationUseCase.createOrganization(command, userId)
        return CreatedResponse(
            id = response.id,
            message = "기관이 성공적으로 등록되었습니다"
        )
    }

    @GetMapping("/{id}")
    suspend fun getOrganization(@PathVariable id: Long): OrganizationResponse {
        return organizationUseCase.getOrganization(id)
    }

    @GetMapping("/serial/{serial}")
    suspend fun getOrganizationBySerial(@PathVariable serial: String): OrganizationResponse {
        return organizationUseCase.getOrganizationBySerial(serial)
    }

    @GetMapping
    fun getAllOrganizations(): Flow<OrganizationListResponse> {
        return organizationUseCase.getAllOrganizations()
    }

    @GetMapping("/search")
    fun searchOrganizations(@RequestParam name: String): Flow<OrganizationListResponse> {
        return organizationUseCase.searchOrganizationsByName(name)
    }

    @PutMapping("/{id}")
    suspend fun updateOrganization(
        @PathVariable id: Long,
        @Valid @RequestBody command: UpdateOrganizationCommand,
        @RequestHeader("X-User-Id", required = false, defaultValue = "system") userId: String
    ): OrganizationResponse {
        return organizationUseCase.updateOrganization(id, command, userId)
    }

    @PatchMapping("/{id}")
    suspend fun patchOrganization(
        @PathVariable id: Long,
        @Valid @RequestBody command: PatchOrganizationCommand,
        @RequestHeader("X-User-Id", required = false, defaultValue = "system") userId: String
    ): OrganizationResponse {
        return organizationUseCase.patchOrganization(id, command, userId)
    }

    @DeleteMapping("/{id}")
    suspend fun deleteOrganization(@PathVariable id: Long): DeletedResponse {
        organizationUseCase.deleteOrganization(id)
        return DeletedResponse(
            id = id,
            message = "기관이 성공적으로 삭제되었습니다"
        )
    }
}
