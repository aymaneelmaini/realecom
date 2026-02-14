package com.aymanegeek.imedia.inventory.application.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.util.UUID

data class CreateInventoryRequest(
    @field:NotNull val productId: UUID,
    @field:NotNull @field:Positive val availableQuantity: Int
)