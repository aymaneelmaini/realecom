package com.aymanegeek.imedia.inventory.application.usecase

import arrow.core.Either
import com.aymanegeek.imedia.common.vo.Quantity
import com.aymanegeek.imedia.inventory.domain.InventoryError
import com.aymanegeek.imedia.product.domain.ProductId

interface ReserveInventoryUsecase {

    fun execute(items: List<InventoryItem>): Either<InventoryError, ReserveInventoryResponse>
}

data class ReserveInventoryResponse(val reservations: List<Reservation>)

data class Reservation(
    val productId: ProductId,
    val reservedQuantity: Quantity,
    val remainingStock: Quantity,
)
