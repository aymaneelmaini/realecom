package com.aymanegeek.imedia.payment.domain

import com.aymanegeek.imedia.common.vo.Price
import com.aymanegeek.imedia.order.domain.OrderId
import org.springframework.data.relational.core.mapping.Embedded
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty.USE_NULL
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.UUID

@Table(name = "payments", schema = "payment_schema")
data class Payment(
    val id: PaymentId? = null,
    val orderId: OrderId,
    @Embedded(prefix = "payment_price_", onEmpty = USE_NULL) val amount: Price,
    val status: PaymentStatus,
    val externalPaymentId: String? = null,
    val createdAt: LocalDateTime? = null
)

enum class PaymentStatus {
    PENDING,
    PROCESSING,
    SUCCESS,
    FAILED
}

@JvmInline
value class PaymentId(val value: UUID)