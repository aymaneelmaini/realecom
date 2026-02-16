package com.aymanegeek.imedia.order.application.usecase.impl

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.aymanegeek.imedia.order.application.usecase.UpdateOrderPaymentStatusRequest
import com.aymanegeek.imedia.order.application.usecase.UpdateOrderPaymentStatusResponse
import com.aymanegeek.imedia.order.application.usecase.UpdateOrderPaymentStatusUseCase
import com.aymanegeek.imedia.order.domain.OrderError
import com.aymanegeek.imedia.order.domain.OrderRepository
import com.aymanegeek.imedia.order.domain.OrderStatus
import com.aymanegeek.imedia.payment.domain.Payment
import com.aymanegeek.imedia.payment.domain.PaymentRepository
import com.aymanegeek.imedia.payment.domain.PaymentStatus
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
        order: com.aymanegeek.imedia.order.domain.Order,
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
