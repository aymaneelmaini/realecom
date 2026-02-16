package com.aymanegeek.imedia.order.application.dto

import java.math.BigDecimal
import java.util.UUID

data class CreateOrderResponse(
    val orderId: UUID,
    val status: String,
    val totalAmount: BigDecimal,
    val currency: String,
    val paymentUrl: String,
    val orderLines: List<OrderLineResponse>
)

data class OrderLineResponse(
    val productId: UUID,
    val quantity: Int,
    val unitPrice: BigDecimal,
    val totalPrice: BigDecimal
)
