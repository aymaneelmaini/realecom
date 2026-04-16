package com.newonedev.easymart.order.application.usecase.impl

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.newonedev.easymart.order.application.usecase.UpdateOrderPaymentStatusRequest
import com.newonedev.easymart.order.application.usecase.UpdateOrderPaymentStatusResponse
import com.newonedev.easymart.order.application.usecase.UpdateOrderPaymentStatusUseCase
import com.newonedev.easymart.order.domain.OrderError
import com.newonedev.easymart.order.domain.OrderRepository
import com.newonedev.easymart.order.domain.OrderStatus
import com.newonedev.easymart.payment.domain.Payment
import com.newonedev.easymart.payment.domain.PaymentRepository
import com.newonedev.easymart.payment.domain.PaymentStatus
import org.springframework.transaction.annotation.Transactional

@Transactional
class DefaultUpdateOrderPaymentStatusUseCase(
    private val orderRepository: OrderRepository,
    private val paymentRepository: PaymentRepository
) : UpdateOrderPaymentStatusUseCase {

    override fun execute(request: UpdateOrderPaymentStatusRequest): Either<OrderError, UpdateOrderPaymentStatusResponse> = either {
        val order = orderRepository.findById(request.orderId).orElse(null)

        ensure(order != null) {
            OrderError.OrderNotFound(request.orderId.value)
        }

        val (orderStatus, paymentStatus) = determineStatuses(request.isPaid)

        orderRepository.save(order.copy(status = orderStatus))
        updateOrCreatePayment(request, order, paymentStatus)

        UpdateOrderPaymentStatusResponse(
            orderId = request.orderId,
            status = orderStatus.name
        )
    }

    private fun determineStatuses(isPaid: Boolean) =
        if (isPaid) OrderStatus.PAID to PaymentStatus.SUCCESS
        else OrderStatus.FAILED to PaymentStatus.FAILED

    private fun updateOrCreatePayment(
        request: UpdateOrderPaymentStatusRequest,
        order: com.newonedev.easymart.order.domain.Order,
        paymentStatus: PaymentStatus
    ) {
        val existingPayment = paymentRepository.findByOrderId(request.orderId)

        val payment = existingPayment?.copy(
            status = paymentStatus,
            externalPaymentId = request.externalPaymentId
        ) ?: Payment(
            orderId = request.orderId,
            amount = order.totalPrice,
            status = paymentStatus,
            externalPaymentId = request.externalPaymentId
        )

        paymentRepository.save(payment)
    }
}
