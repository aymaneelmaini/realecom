package com.newonedev.easymart.order.application.usecase

import arrow.core.Either
import com.newonedev.easymart.order.domain.OrderError
import com.newonedev.easymart.order.domain.OrderId

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
