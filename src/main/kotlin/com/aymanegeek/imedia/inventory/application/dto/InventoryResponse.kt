package com.aymanegeek.imedia.inventory.application.dto

import java.util.UUID

data class InventoryResponse(
    val inventoryId: UUID,
    val productId: UUID,
    val availableQuantity: Int,
    val reservedQuantity: Int,
)