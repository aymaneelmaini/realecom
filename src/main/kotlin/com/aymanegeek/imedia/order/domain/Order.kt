package com.aymanegeek.imedia.order.domain

import com.aymanegeek.imedia.order.domain.OrderStatus.PENDING
import com.aymanegeek.imedia.common.vo.Price
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Table("orders")
data class Order(
    @Id val id: OrderId? = null,
    val status: OrderStatus = PENDING,
    val orderLines: MutableList<OrderLine> = mutableListOf(),
    val createdAt: LocalDateTime? = null,
) {
    val totalPrice: Price
        get() = orderLines
            .map { it.price.amount * it.quantity.value.toBigDecimal() }
            .fold(BigDecimal.ZERO) { acc, p -> acc + p }
            .let { Price(it, "USD") }

    fun addItem(line: OrderLine) {
        orderLines.add(line)
    }
}


enum class OrderStatus {
    PENDING,      // order created, waiting to reserve inventory
    RESERVED,     // inventory reserved
    PAID,         // payment successful
    CONFIRMED,    // order confirmed, ready to ship
    FAILED,       // inventory reserve or payment failed
    CANCELLED     // user cancelled
}

@JvmInline
value class OrderId(val value: UUID)

