package com.newonedev.easymart.inventory.domain

import com.newonedev.easymart.common.vo.ProductId
import org.springframework.data.repository.CrudRepository
import java.util.*

interface InventoryRepository : CrudRepository<Inventory, InventoryId> {
    fun findByProductId(productId: ProductId): Optional<Inventory>
    fun findAllByProductIdIn(productIds: List<ProductId>): List<Inventory>
    fun existsByProductId(productId: ProductId): Boolean
}