package com.aymanegeek.imedia.inventory.application.usecase.impl

import arrow.core.left
import arrow.core.right
import com.aymanegeek.imedia.common.vo.ProductId
import com.aymanegeek.imedia.common.vo.Quantity
import com.aymanegeek.imedia.inventory.application.usecase.InventoryItem
import com.aymanegeek.imedia.inventory.application.usecase.ItemAvailability
import com.aymanegeek.imedia.inventory.application.usecase.VerifyInventoryAvailabilityUsecase
import com.aymanegeek.imedia.inventory.application.usecase.VerifyInventoryResponse
import com.aymanegeek.imedia.inventory.domain.Inventory
import com.aymanegeek.imedia.inventory.domain.InventoryError
import com.aymanegeek.imedia.inventory.domain.InventoryRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class DefaultReserveInventoryUsecaseTest : StringSpec({

    val inventoryRepository: InventoryRepository = mockk(relaxed = true)
    val verifyInventoryAvailabilityUsecase: VerifyInventoryAvailabilityUsecase = mockk()
    val sut = DefaultReserveInventoryUsecase(inventoryRepository, verifyInventoryAvailabilityUsecase)

    val productId = ProductId.generate()
    val requestedQty = Quantity(3)
    val availableQty = Quantity(10)

    val item = InventoryItem(productId, requestedQty)
    val inventory = Inventory.create(productId, availableQty)

    "reserves inventory and returns the remaining stock correctly" {
        every { verifyInventoryAvailabilityUsecase.execute(listOf(item)) } returns VerifyInventoryResponse(
            availableItems = listOf(ItemAvailability(productId, requestedQty, availableQty)),
            unavailableItems = emptyList()
        ).right()
        every { inventoryRepository.findAllByProductIdIn(listOf(productId)) } returns listOf(inventory)
        every { inventoryRepository.save(any()) } answers { firstArg() }

        val response = sut.execute(listOf(item)).getOrNull()!!

        val reservation = response.reservations.first()
        reservation.productId shouldBe productId
        reservation.reservedQuantity shouldBe requestedQty
        reservation.remainingStock shouldBe Quantity(availableQty.value - requestedQty.value)
    }

    "saves the updated inventory after reservation" {
        every { verifyInventoryAvailabilityUsecase.execute(listOf(item)) } returns VerifyInventoryResponse(
            availableItems = listOf(ItemAvailability(productId, requestedQty, availableQty)),
            unavailableItems = emptyList()
        ).right()
        every { inventoryRepository.findAllByProductIdIn(listOf(productId)) } returns listOf(inventory)
        every { inventoryRepository.save(any()) } answers { firstArg() }

        sut.execute(listOf(item))

        verify { inventoryRepository.save(match { it.availableQuantity == Quantity(7) && it.reservedQuantity == requestedQty }) }
    }

    "returns InsufficientInventory error when stock is not enough" {
        val unavailableItem = ItemAvailability(productId, requestedQty, Quantity(1))
        every { verifyInventoryAvailabilityUsecase.execute(listOf(item)) } returns VerifyInventoryResponse(
            availableItems = emptyList(),
            unavailableItems = listOf(unavailableItem)
        ).right()

        val error = sut.execute(listOf(item)).leftOrNull()

        error.shouldBeInstanceOf<InventoryError.InsufficientInventory>()
        error.productId shouldBe productId.value
        error.available shouldBe 1
        error.requested shouldBe requestedQty.value
    }

    "propagates error from the availability check" {
        val inventoryError = InventoryError.InventoryNotFoundForProduct(productId.value)
        every { verifyInventoryAvailabilityUsecase.execute(listOf(item)) } returns inventoryError.left()

        val result = sut.execute(listOf(item))

        result.isLeft() shouldBe true
        result.leftOrNull() shouldBe inventoryError
    }
})
