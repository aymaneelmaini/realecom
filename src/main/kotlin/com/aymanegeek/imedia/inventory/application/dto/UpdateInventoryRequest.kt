package com.aymanegeek.imedia.inventory.application.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import java.util.UUID

data class UpdateInventoryRequest(
    @field:NotNull val productId: UUID,
    @field:NotNull @field:PositiveOrZero val availableQuantity: Int,
    @field:NotNull @field:PositiveOrZero val reservedQuantity: Int
)