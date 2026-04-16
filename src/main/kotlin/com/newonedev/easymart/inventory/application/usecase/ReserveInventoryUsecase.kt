package com.newonedev.easymart.inventory.application.usecase

import arrow.core.Either
import com.newonedev.easymart.common.vo.Quantity
import com.newonedev.easymart.inventory.domain.InventoryError
import com.newonedev.easymart.common.vo.ProductId

interface ReserveInventoryUsecase {

    fun execute(items: List<InventoryItem>): Either<InventoryError, ReserveInventoryResponse>
}

data class ReserveInventoryResponse(val reservations: List<Reservation>)

data class Reservation(
    val productId: ProductId,
    val reservedQuantity: Quantity,
    val remainingStock: Quantity,
)
