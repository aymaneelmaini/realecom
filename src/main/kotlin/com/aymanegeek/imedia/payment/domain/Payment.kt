package com.aymanegeek.imedia.payment.domain

import com.aymanegeek.imedia.common.vo.Price
import com.aymanegeek.imedia.order.domain.OrderId
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.UUID

@Table("payment_schema.payments")
data class Payment(
    val id: PaymentId? = null,
    val orderId: OrderId,
    val amount: Price,
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