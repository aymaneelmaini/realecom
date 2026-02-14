package com.aymanegeek.imedia.inventory.domain

import com.aymanegeek.imedia.product.domain.ProductId
import org.springframework.data.repository.CrudRepository
import java.util.*

interface InventoryRepository : CrudRepository<Inventory, InventoryId> {
    fun findByProductId(productId: ProductId): Optional<Inventory>
    fun existsByProductId(productId: ProductId): Boolean
}