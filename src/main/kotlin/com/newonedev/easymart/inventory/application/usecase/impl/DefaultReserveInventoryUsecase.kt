package com.newonedev.easymart.inventory.application.usecase.impl

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.newonedev.easymart.common.vo.Quantity
import com.newonedev.easymart.inventory.application.usecase.InventoryItem
import com.newonedev.easymart.inventory.application.usecase.Reservation
import com.newonedev.easymart.inventory.application.usecase.ReserveInventoryResponse
import com.newonedev.easymart.inventory.application.usecase.ReserveInventoryUsecase
import com.newonedev.easymart.inventory.application.usecase.VerifyInventoryAvailabilityUsecase
import com.newonedev.easymart.inventory.application.usecase.VerifyInventoryResponse
import com.newonedev.easymart.inventory.domain.InventoryError
import com.newonedev.easymart.inventory.domain.InventoryRepository
import com.newonedev.easymart.common.vo.ProductId
import org.springframework.transaction.annotation.Transactional

@Transactional
class DefaultReserveInventoryUsecase(
    private val inventoryRepository: InventoryRepository,
    private val verifyInventoryAvailabilityUsecase: VerifyInventoryAvailabilityUsecase
) : ReserveInventoryUsecase {

    override fun execute(items: List<InventoryItem>): Either<InventoryError, ReserveInventoryResponse> = either {
        verifyInventoryAvailabilityUsecase.execute(items).bind()
            .also { ensureAllAvailable(it) }

        val inventoriesMap = fetchInventories(items)
        val reservations = items.map { reserveItem(it, inventoriesMap) }

        ReserveInventoryResponse(reservations)
    }

    private fun Raise<InventoryError>.ensureAllAvailable(verification: VerifyInventoryResponse) {
        ensure(verification.allAvailable) {
            val firstUnavailable = verification.unavailableItems.first()
            InventoryError.InsufficientInventory(
                productId = firstUnavailable.productId.value,
                available = firstUnavailable.availableQuantity.value,
                requested = firstUnavailable.requestedQuantity.value
            )
        }
    }

    private fun fetchInventories(items: List<InventoryItem>) =
        items.map(InventoryItem::productId)
            .let(inventoryRepository::findAllByProductIdIn)
            .associateBy { it.productId }

    private fun reserveItem(
        item: InventoryItem,
        inventoriesMap: Map<ProductId, com.newonedev.easymart.inventory.domain.Inventory>
    ): Reservation {
        val inventory = inventoriesMap.getValue(item.productId)
        val newAvailableQuantity = Quantity(inventory.availableQuantity.value - item.quantity.value)
        val newReservedQuantity = Quantity(inventory.reservedQuantity.value + item.quantity.value)

        inventoryRepository.save(
            inventory.copy(
                availableQuantity = newAvailableQuantity,
                reservedQuantity = newReservedQuantity
            )
        )

        return Reservation(
            productId = item.productId,
            reservedQuantity = item.quantity,
            remainingStock = newAvailableQuantity
        )
    }
}