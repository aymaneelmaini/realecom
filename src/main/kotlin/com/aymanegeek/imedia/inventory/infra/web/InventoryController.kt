package com.aymanegeek.imedia.inventory.infra.web

import com.aymanegeek.imedia.common.error.DomainErrorException
import com.aymanegeek.imedia.inventory.application.dto.CreateInventoryRequest
import com.aymanegeek.imedia.inventory.application.dto.InventoryResponse
import com.aymanegeek.imedia.inventory.application.dto.UpdateInventoryRequest
import com.aymanegeek.imedia.inventory.application.service.InventoryService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/inventories")
class InventoryController(
    private val inventoryService: InventoryService
) {

    @PostMapping
    fun createInventory(@Valid @RequestBody request: CreateInventoryRequest): ResponseEntity<InventoryResponse> {
        return inventoryService.createInventory(request)
            .fold(
                ifLeft = { error -> throw DomainErrorException(error) },
                ifRight = { ResponseEntity(it, HttpStatus.CREATED) }
            )
    }

    @PutMapping
    fun updateInventory(@Valid @RequestBody request: UpdateInventoryRequest): ResponseEntity<InventoryResponse> {
        return inventoryService.updateInventory(request)
            .fold(
                ifLeft = { error -> throw DomainErrorException(error) },
                ifRight = { ResponseEntity.ok(it) }
            )
    }

    @GetMapping("/product/{productId}")
    fun findByProductId(@PathVariable productId: UUID): ResponseEntity<InventoryResponse> {
        return inventoryService.findByProductId(productId)
            .fold(
                ifLeft = { error -> throw DomainErrorException(error) },
                ifRight = { ResponseEntity.ok(it) }
            )
    }

    @GetMapping
    fun findAll(): ResponseEntity<List<InventoryResponse>> {
        return inventoryService.findAll()
            .fold(
                ifLeft = { error -> throw DomainErrorException(error) },
                ifRight = { ResponseEntity.ok(it) }
            )
    }
}
