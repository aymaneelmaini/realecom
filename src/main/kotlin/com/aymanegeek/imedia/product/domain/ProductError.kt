package com.aymanegeek.imedia.product.domain

import com.aymanegeek.imedia.common.error.DomainError
import org.springframework.http.HttpStatus
import java.util.*

sealed interface ProductError : DomainError {

    data class ProductNotFound(val productId: UUID) : ProductError {
        override val message = "Product with id '$productId' not found"
        override val errorCode = "PRODUCT_NOT_FOUND"
        override val httpStatus = HttpStatus.NOT_FOUND
    }

    data class ProductNameAlreadyExists(val name: String) : ProductError {
        override val message = "Product with name '$name' already exists"
        override val errorCode = "PRODUCT_NAME_EXISTS"
        override val httpStatus = HttpStatus.CONFLICT
    }

    data class InvalidPrice(val reason: String) : ProductError {
        override val message = "Invalid price: $reason"
        override val errorCode = "INVALID_PRICE"
        override val httpStatus = HttpStatus.UNPROCESSABLE_ENTITY
    }

    data class InvalidProductName(val reason: String) : ProductError {
        override val message = "Invalid product name: $reason"
        override val errorCode = "INVALID_PRODUCT_NAME"
        override val httpStatus = HttpStatus.UNPROCESSABLE_ENTITY
    }
}
