package com.aymanegeek.imedia.inventory.domain

import com.aymanegeek.imedia.common.vo.Quantity
import com.aymanegeek.imedia.product.domain.ProductId
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("inventory_schema.inventories")
data class Inventory(
    val id: InventoryId? = null,
    val productId: ProductId,
    val availableQuantity: Quantity,
    val reservedQuantity: Quantity

)

@JvmInline
value class InventoryId(val value: UUID)

