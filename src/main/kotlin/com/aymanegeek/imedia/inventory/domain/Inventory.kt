package com.aymanegeek.imedia.inventory.domain

import com.aymanegeek.imedia.common.vo.Quantity
import com.aymanegeek.imedia.common.vo.ProductId
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table(name = "inventories", schema = "inventory_schema")
data class Inventory(
    @Id val id: InventoryId,
    val productId: ProductId,
    val availableQuantity: Quantity,
    val reservedQuantity: Quantity,
    val version: Long = 0
) {
    companion object {
        fun create(
            productId: ProductId,
            availableQuantity: Quantity,
            reservedQuantity: Quantity = Quantity(0)
        ) = Inventory(
            id = InventoryId.generate(),
            productId = productId,
            availableQuantity = availableQuantity,
            reservedQuantity = reservedQuantity,
            version = 0
        )
    }
}

@JvmInline
value class InventoryId(val value: UUID) {
    companion object {
        fun generate() = InventoryId(UUID.randomUUID())
    }
}

