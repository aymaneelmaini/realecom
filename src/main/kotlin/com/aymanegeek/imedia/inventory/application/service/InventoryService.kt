package com.aymanegeek.imedia.inventory.application.service

import arrow.core.Either
import com.aymanegeek.imedia.inventory.application.dto.CreateInventoryRequest
import com.aymanegeek.imedia.inventory.application.dto.InventoryResponse
import com.aymanegeek.imedia.inventory.application.dto.UpdateInventoryRequest
import com.aymanegeek.imedia.inventory.domain.InventoryError
import java.util.UUID

interface InventoryService {
    fun createInventory(request: CreateInventoryRequest): Either<InventoryError, InventoryResponse>

    fun updateInventory(request: UpdateInventoryRequest): Either<InventoryError, InventoryResponse>

    fun findByProductId(id: UUID): Either<InventoryError, InventoryResponse>

    fun findAll(): Either<InventoryError, List<InventoryResponse>>
}