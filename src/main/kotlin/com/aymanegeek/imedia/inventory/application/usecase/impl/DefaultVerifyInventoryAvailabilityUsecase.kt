package com.aymanegeek.imedia.inventory.application.usecase.impl

import arrow.core.Either
import arrow.core.raise.either
import com.aymanegeek.imedia.common.vo.Quantity
import com.aymanegeek.imedia.inventory.application.usecase.InventoryItem
import com.aymanegeek.imedia.inventory.application.usecase.ItemAvailability
import com.aymanegeek.imedia.inventory.application.usecase.VerifyInventoryAvailabilityUsecase
import com.aymanegeek.imedia.inventory.application.usecase.VerifyInventoryResponse
import com.aymanegeek.imedia.inventory.domain.Inventory
import com.aymanegeek.imedia.inventory.domain.InventoryError
import com.aymanegeek.imedia.inventory.domain.InventoryRepository
import com.aymanegeek.imedia.common.vo.ProductId
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
class DefaultVerifyInventoryAvailabilityUsecase(
    private val inventoryRepository: InventoryRepository
) : VerifyInventoryAvailabilityUsecase {

    override fun execute(items: List<InventoryItem>): Either<InventoryError, VerifyInventoryResponse> = either {

        val productIds = items.map { it.productId }
        val inventories = inventoryRepository.findAllByProductIdIn(productIds)
        val inventoryMap = inventories.associateBy { it.productId }

        val (available, unavailable) = items.partition { item ->
            val inventory = inventoryMap[item.productId]
            inventory != null && inventory.availableQuantity.value >= item.quantity.value
        }
        val availableItems = getAvailableItems(available, inventoryMap)
        val unavailableItems = getUnavailableItems(unavailable, inventoryMap)

        VerifyInventoryResponse(availableItems, unavailableItems)
    }

    private fun getUnavailableItems(
        unavailable: List<InventoryItem>,
        inventoryMap: Map<ProductId, Inventory>
    ): List<ItemAvailability> = unavailable.map { item ->
        val inventory = inventoryMap[item.productId]
        ItemAvailability(
            productId = item.productId,
            requestedQuantity = item.quantity,
            availableQuantity = inventory?.availableQuantity ?: Quantity.ZERO,
        )
    }

    private fun getAvailableItems(
        available: List<InventoryItem>,
        inventoryMap: Map<ProductId, Inventory>
    ): List<ItemAvailability> = available.map { item ->
        val inventory = inventoryMap[item.productId]!!
        ItemAvailability(
            productId = item.productId,
            requestedQuantity = item.quantity,
            availableQuantity = inventory.availableQuantity
        )
    }
}