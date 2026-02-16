package com.aymanegeek.imedia.payment.domain

import arrow.core.Either
import com.aymanegeek.imedia.common.vo.Price
import com.aymanegeek.imedia.order.domain.OrderId

interface PaymentProcessor {

    fun createPaymentLink(
        orderId: OrderId,
        lineItems: List<PaymentLineItem>,
        description: String = "Order Payment"
    ): Either<PaymentError, PaymentLinkResult>
}

data class PaymentLineItem(
    val name: String,
    val unitPrice: Price,
    val quantity: Int
)

data class PaymentLinkResult(
    val paymentUrl: String,
    val externalPaymentId: String
)

sealed interface PaymentError {
    val message: String

    data class ProcessorError(override val message: String) : PaymentError
}