package com.aymanegeek.imedia.order.application.dto

import com.aymanegeek.imedia.payment.domain.PaymentMethod
import org.jetbrains.annotations.NotNull
import java.util.*

data class CreateOrderRequest(
    @field:NotNull val orderLines: List<OrderLineRequest>,
    val paymentMethod: PaymentMethod = PaymentMethod.STRIPE
)

data class OrderLineRequest(
    @field:NotNull val productId: UUID,
    @field:NotNull val quantity: Int = 1
)