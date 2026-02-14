package com.aymanegeek.imedia.inventory.domain

import com.aymanegeek.imedia.common.error.DomainError
import org.springframework.http.HttpStatus
import java.util.*

sealed interface InventoryError : DomainError {

    data class InventoryNotFound(val inventoryId: UUID) : InventoryError {
        override val message = "Inventory with id '$inventoryId' not found"
        override val errorCode = "INVENTORY_NOT_FOUND"
        override val httpStatus = HttpStatus.NOT_FOUND
    }

    data class InventoryNotFoundForProduct(val productId: UUID) : InventoryError {
        override val message = "Inventory for product '$productId' not found"
        override val errorCode = "INVENTORY_NOT_FOUND_FOR_PRODUCT"
        override val httpStatus = HttpStatus.NOT_FOUND
    }

    data class InventoryAlreadyExists(val productId: UUID) : InventoryError {
        override val message = "Inventory for product '$productId' already exists"
        override val errorCode = "INVENTORY_ALREADY_EXISTS"
        override val httpStatus = HttpStatus.CONFLICT
    }

    data class ProductNotFound(val productId: UUID) : InventoryError {
        override val message = "Product with id '$productId' not found"
        override val errorCode = "PRODUCT_NOT_FOUND"
        override val httpStatus = HttpStatus.NOT_FOUND
    }

    data class InvalidQuantity(val reason: String) : InventoryError {
        override val message = "Invalid quantity: $reason"
        override val errorCode = "INVALID_QUANTITY"
        override val httpStatus = HttpStatus.UNPROCESSABLE_ENTITY
    }

    data class InsufficientInventory(val productId: UUID, val available: Int, val requested: Int) : InventoryError {
        override val message = "Insufficient inventory for product '$productId': available=$available, requested=$requested"
        override val errorCode = "INSUFFICIENT_INVENTORY"
        override val httpStatus = HttpStatus.UNPROCESSABLE_ENTITY
    }
}
