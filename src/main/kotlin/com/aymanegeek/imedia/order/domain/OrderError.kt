package com.aymanegeek.imedia.order.domain

import com.aymanegeek.imedia.common.error.DomainError
import com.aymanegeek.imedia.product.domain.ProductId
import org.springframework.http.HttpStatus
import java.util.UUID

sealed interface OrderError : DomainError {
    data class InvalidOrderLines(override val message: String) : OrderError {
        override val errorCode = "INVALID_ORDER_LINES"
        override val httpStatus = HttpStatus.UNPROCESSABLE_ENTITY
    }

    data class ProductsNotAvailable(val missingProducts: Set<ProductId>) : OrderError {
        override val message = "Some of the requested products are not available"
        override val errorCode = "PRODUCTS_NOT_AVAILABLE"
        override val httpStatus = HttpStatus.NOT_FOUND
        override val details = missingProducts.map {
            mapOf("productId" to it.value)
        }
    }

    data class InsufficientStock(val unavailableItems: List<InsufficientStockItem>) : OrderError {
        override val message = "Insufficient stock for ${unavailableItems.size} product(s)"
        override val errorCode = "INSUFFICIENT_STOCK"
        override val httpStatus = HttpStatus.UNPROCESSABLE_ENTITY
        override val details = unavailableItems
    }

    data class PaymentCreationFailed(override val message: String) : OrderError {
        override val errorCode = "PAYMENT_CREATION_FAILED"
        override val httpStatus = HttpStatus.INTERNAL_SERVER_ERROR
    }

    data class OrderNotFound(val orderId: UUID) : OrderError {
        override val message = "Order with id '$orderId' not found"
        override val errorCode = "ORDER_NOT_FOUND"
        override val httpStatus = HttpStatus.NOT_FOUND
    }
}

data class InsufficientStockItem(
    val productId: java.util.UUID,
    val requested: Int,
    val available: Int
)