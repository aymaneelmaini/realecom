package com.newonedev.easymart.inventory.application.service

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.newonedev.easymart.common.vo.Quantity
import com.newonedev.easymart.inventory.application.dto.CreateInventoryRequest
import com.newonedev.easymart.inventory.application.dto.InventoryResponse
import com.newonedev.easymart.inventory.application.dto.UpdateInventoryRequest
import com.newonedev.easymart.inventory.domain.Inventory
import com.newonedev.easymart.inventory.domain.InventoryError
import com.newonedev.easymart.inventory.domain.InventoryRepository
import com.newonedev.easymart.common.vo.ProductId
import com.newonedev.easymart.product.domain.ProductRepository
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
class DefaultInventoryService(
    private val inventoryRepository: InventoryRepository,
    private val jdbcTemplate: JdbcAggregateTemplate,
    private val productRepository: ProductRepository
) : InventoryService {

    override fun createInventory(request: CreateInventoryRequest): Either<InventoryError, InventoryResponse> = either {
        val productId = ProductId(request.productId)

        ensure(productRepository.existsById(productId)) {
            InventoryError.ProductNotFound(request.productId)
        }

        ensure(!inventoryRepository.existsByProductId(productId)) {
            InventoryError.InventoryAlreadyExists(request.productId)
        }

        ensure(request.availableQuantity > 0) {
            InventoryError.InvalidQuantity("Available quantity must be positive")
        }

        val inventory = Inventory.create(
            productId = productId,
            availableQuantity = Quantity(request.availableQuantity)
        )

        val saved = jdbcTemplate.insert(inventory)
        saved.toResponse()
    }

    override fun updateInventory(request: UpdateInventoryRequest): Either<InventoryError, InventoryResponse> = either {
        val productId = ProductId(request.productId)

        val inventory = inventoryRepository.findByProductId(productId)
            .orElse(null) ?: raise(InventoryError.InventoryNotFoundForProduct(request.productId))

        ensure(request.availableQuantity >= 0) {
            InventoryError.InvalidQuantity("Available quantity cannot be negative")
        }

        ensure(request.reservedQuantity >= 0) {
            InventoryError.InvalidQuantity("Reserved quantity cannot be negative")
        }

        val updated = inventory.copy(
            availableQuantity = Quantity(request.availableQuantity),
            reservedQuantity = Quantity(request.reservedQuantity)
        )

        val saved = inventoryRepository.save(updated)
        saved.toResponse()
    }

    override fun findByProductId(id: UUID): Either<InventoryError, InventoryResponse> = either {
        val productId = ProductId(id)

        val inventory = inventoryRepository.findByProductId(productId)
            .orElse(null) ?: raise(InventoryError.InventoryNotFoundForProduct(id))

        inventory.toResponse()
    }

    override fun findAll(): Either<InventoryError, List<InventoryResponse>> = either {
        inventoryRepository.findAll()
            .map { it.toResponse() }
            .toList()
    }

    private fun Inventory.toResponse() = InventoryResponse(
        inventoryId = id.value,
        productId = productId.value,
        availableQuantity = availableQuantity.value,
        reservedQuantity = reservedQuantity.value
    )
}
