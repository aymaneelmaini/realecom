package com.aymanegeek.imedia.order.domain

import com.aymanegeek.imedia.common.vo.Quantity
import com.aymanegeek.imedia.common.vo.Price
import com.aymanegeek.imedia.product.domain.ProductId
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.*

@Table("order_lines")
data class OrderLine(
    @Id val id: OrderLineId,
    val orderId: OrderId,
    val productId: ProductId,
    val quantity: Quantity,
    val price: Price,
    val createdAt: LocalDateTime? = null
)

@JvmInline
value class OrderLineId(val value: UUID)
