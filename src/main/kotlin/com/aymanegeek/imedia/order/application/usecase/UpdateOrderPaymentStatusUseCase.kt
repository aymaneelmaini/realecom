package com.aymanegeek.imedia.order.application.usecase

import arrow.core.Either
import com.aymanegeek.imedia.order.domain.OrderError
import com.aymanegeek.imedia.order.domain.OrderId

interface UpdateOrderPaymentStatusUseCase {
    fun execute(request: UpdateOrderPaymentStatusRequest): Either<OrderError, UpdateOrderPaymentStatusResponse>
}

data class UpdateOrderPaymentStatusRequest(
    val orderId: OrderId,
    val isPaid: Boolean,
    val externalPaymentId: String
)

data class UpdateOrderPaymentStatusResponse(
    val orderId: OrderId,
    val status: String
)
