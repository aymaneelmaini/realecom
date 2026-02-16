package com.aymanegeek.imedia.order.domain

import com.aymanegeek.imedia.common.vo.Quantity
import com.aymanegeek.imedia.common.vo.Price
import com.aymanegeek.imedia.common.vo.ProductId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.ReadOnlyProperty
import org.springframework.data.relational.core.mapping.Embedded
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty.USE_NULL
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.*

@Table(name = "order_lines", schema = "order_schema")
data class OrderLine(
    @Id val id: OrderLineId,
    val orderId: OrderId,
    val productId: ProductId,
    val quantity: Quantity,
    @Embedded(prefix = "order_line_price_", onEmpty = USE_NULL) val price: Price,
    @ReadOnlyProperty val createdAt: LocalDateTime? = null
)

@JvmInline
value class OrderLineId(val value: UUID) {
    companion object {
        fun generate() = OrderLineId(UUID.randomUUID())
    }
}
