package com.newonedev.easymart.inventory.application.service

import arrow.core.Either
import com.newonedev.easymart.inventory.application.dto.CreateInventoryRequest
import com.newonedev.easymart.inventory.application.dto.InventoryResponse
import com.newonedev.easymart.inventory.application.dto.UpdateInventoryRequest
import com.newonedev.easymart.inventory.domain.InventoryError
import java.util.UUID

interface InventoryService {
    fun createInventory(request: CreateInventoryRequest): Either<InventoryError, InventoryResponse>

    fun updateInventory(request: UpdateInventoryRequest): Either<InventoryError, InventoryResponse>

    fun findByProductId(id: UUID): Either<InventoryError, InventoryResponse>

    fun findAll(): Either<InventoryError, List<InventoryResponse>>
}