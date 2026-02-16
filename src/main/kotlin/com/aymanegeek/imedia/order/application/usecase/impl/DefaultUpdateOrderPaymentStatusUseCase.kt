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
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DefaultUpdateOrderPaymentStatusUseCase(
    private val orderRepository: OrderRepository,
    private val paymentRepository: PaymentRepository
) : UpdateOrderPaymentStatusUseCase {

    @Transactional
    override fun execute(request: UpdateOrderPaymentStatusRequest): Either<OrderError, UpdateOrderPaymentStatusResponse> = either {
        val order = orderRepository.findById(request.orderId).orElse(null)

        ensure(order != null) {
            OrderError.OrderNotFound(request.orderId.value)
        }

        val newOrderStatus = if (request.isPaid) OrderStatus.PAID else OrderStatus.FAILED
        val newPaymentStatus = if (request.isPaid) PaymentStatus.SUCCESS else PaymentStatus.FAILED

        // Update order status
        val updatedOrder = order.copy(status = newOrderStatus)
        orderRepository.save(updatedOrder)

        // Find or create payment record
        val existingPayment = paymentRepository.findByOrderId(request.orderId)
        if (existingPayment != null) {
            paymentRepository.save(
                existingPayment.copy(
                    status = newPaymentStatus,
                    externalPaymentId = request.externalPaymentId
                )
            )
        } else {
            paymentRepository.save(
                Payment(
                    orderId = request.orderId,
                    amount = order.totalPrice,
                    status = newPaymentStatus,
                    externalPaymentId = request.externalPaymentId
                )
            )
        }

        UpdateOrderPaymentStatusResponse(
            orderId = request.orderId,
            status = newOrderStatus.name
        )
    }
}
