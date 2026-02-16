package com.aymanegeek.imedia.inventory.application.usecase.impl

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.aymanegeek.imedia.common.vo.Quantity
import com.aymanegeek.imedia.inventory.application.usecase.InventoryItem
import com.aymanegeek.imedia.inventory.application.usecase.Reservation
import com.aymanegeek.imedia.inventory.application.usecase.ReserveInventoryResponse
import com.aymanegeek.imedia.inventory.application.usecase.ReserveInventoryUsecase
import com.aymanegeek.imedia.inventory.application.usecase.VerifyInventoryAvailabilityUsecase
import com.aymanegeek.imedia.inventory.domain.InventoryError
import com.aymanegeek.imedia.inventory.domain.InventoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DefaultReserveInventoryUsecase(
    private val inventoryRepository: InventoryRepository,
    private val verifyInventoryAvailabilityUsecase: VerifyInventoryAvailabilityUsecase
) : ReserveInventoryUsecase {

    @Transactional
    override fun execute(items: List<InventoryItem>): Either<InventoryError, ReserveInventoryResponse> = either {

        val verification = verifyInventoryAvailabilityUsecase.execute(items).bind()

        ensure(verification.allAvailable) {
            val firstUnavailable = verification.unavailableItems.first()
            InventoryError.InsufficientInventory(
                productId = firstUnavailable.productId.value,
                available = firstUnavailable.availableQuantity.value,
                requested = firstUnavailable.requestedQuantity.value
            )
        }

        val productIds = items.map { it.productId }
        val inventories = inventoryRepository.findAllByProductIdIn(productIds)
        val inventoriesMap = inventories.associateBy { it.productId }

        val reservations = items.map { item ->
            val inventory = inventoriesMap[item.productId]!!
            val newAvailableQuantity = Quantity(inventory.availableQuantity.value - item.quantity.value)
            val newReservedQuantity = Quantity(inventory.reservedQuantity.value + item.quantity.value)

            val updated = inventoryRepository.save(
                inventory.copy(
                    availableQuantity = newAvailableQuantity,
                    reservedQuantity = newReservedQuantity
                )
            )

            Reservation(
                productId = updated.productId,
                reservedQuantity = Quantity(item.quantity.value),
                remainingStock = newAvailableQuantity
            )
        }

        ReserveInventoryResponse(reservations = reservations)
    }
}