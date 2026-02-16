package com.aymanegeek.imedia.order.domain

import com.aymanegeek.imedia.common.vo.Price
import com.aymanegeek.imedia.order.domain.OrderStatus.PENDING
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.ReadOnlyProperty
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Table(name = "orders", schema = "order_schema")
data class Order(
    @Id val id: OrderId,
    @Column("status")
    val status: OrderStatus = PENDING,
    @MappedCollection(idColumn = "order_id")
    val orderLines: MutableSet<OrderLine> = mutableSetOf(),
    @ReadOnlyProperty val createdAt: LocalDateTime? = null,
) {
    val totalPrice: Price
        get() = orderLines
            .map { it.price.amount * it.quantity.value.toBigDecimal() }
            .fold(BigDecimal.ZERO) { acc, p -> acc + p }
            .let { Price(it, "USD") }

    fun addItem(line: OrderLine) {
        orderLines.add(line)
    }

    companion object {
        fun create(
            id: OrderId = OrderId.generate(),
            orderLines: MutableSet<OrderLine>
        ) = Order(
            id = id,
            status = PENDING,
            orderLines = orderLines
        )
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
value class OrderId(val value: UUID) {
    companion object {
        fun generate() = OrderId(UUID.randomUUID())
    }
}

