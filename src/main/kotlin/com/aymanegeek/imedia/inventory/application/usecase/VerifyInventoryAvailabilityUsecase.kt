package com.aymanegeek.imedia.inventory.application.usecase

import arrow.core.Either
import com.aymanegeek.imedia.common.vo.Quantity
import com.aymanegeek.imedia.inventory.domain.InventoryError
import com.aymanegeek.imedia.product.domain.ProductId

interface VerifyInventoryAvailabilityUsecase {
    fun execute(items: List<InventoryItem>): Either<InventoryError, VerifyInventoryResponse>
}

data class InventoryItem(
    val productId: ProductId,
    val quantity: Quantity
)

data class VerifyInventoryResponse(
    val availableItems: List<ItemAvailability>,
    val unavailableItems: List<ItemAvailability>,
) {
    val allAvailable = unavailableItems.isEmpty()
}

data class ItemAvailability(
    val productId: ProductId,
    val requestedQuantity: Quantity,
    val availableQuantity: Quantity,
)