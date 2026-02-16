package com.aymanegeek.imedia.payment.infra.stripe

import arrow.core.Either
import arrow.core.raise.either
import com.aymanegeek.imedia.order.domain.OrderId
import com.aymanegeek.imedia.payment.domain.PaymentError
import com.aymanegeek.imedia.payment.domain.PaymentLineItem
import com.aymanegeek.imedia.payment.domain.PaymentLinkResult
import com.aymanegeek.imedia.payment.domain.PaymentProcessor
import com.stripe.model.PaymentLink
import com.stripe.model.Price as StripePrice
import com.stripe.param.PaymentLinkCreateParams
import com.stripe.param.PriceCreateParams
import org.springframework.stereotype.Component

@Component
class StripePaymentProcessor : PaymentProcessor {

    override fun createPaymentLink(
        orderId: OrderId,
        lineItems: List<PaymentLineItem>,
        description: String
    ): Either<PaymentError, PaymentLinkResult> = either {
        try {
            val paymentLinkBuilder = PaymentLinkCreateParams.builder()

            lineItems.forEach { item ->
                val amountInCents = (item.unitPrice.amount.toDouble() * 100).toLong()

                val priceParams = PriceCreateParams.builder()
                    .setCurrency(item.unitPrice.currency.lowercase())
                    .setUnitAmount(amountInCents)
                    .setProductData(
                        PriceCreateParams.ProductData.builder()
                            .setName(item.name)
                            .build()
                    )
                    .build()

                val stripePrice = StripePrice.create(priceParams)

                paymentLinkBuilder.addLineItem(
                    PaymentLinkCreateParams.LineItem.builder()
                        .setPrice(stripePrice.id)
                        .setQuantity(item.quantity.toLong())
                        .build()
                )
            }

            val paymentLinkParams = paymentLinkBuilder
                .setAfterCompletion(
                    PaymentLinkCreateParams.AfterCompletion.builder()
                        .setType(PaymentLinkCreateParams.AfterCompletion.Type.REDIRECT)
                        .setRedirect(
                            PaymentLinkCreateParams.AfterCompletion.Redirect.builder()
                                .setUrl("http://localhost:3000/orders/${orderId.value}/success")
                                .build()
                        )
                        .build()
                )
                .putMetadata("order_id", orderId.value.toString())
                .build()

            val paymentLink = PaymentLink.create(paymentLinkParams)

            PaymentLinkResult(
                paymentUrl = paymentLink.url,
                externalPaymentId = paymentLink.id
            )

        } catch (e: Exception) {
            raise(PaymentError.ProcessorError("Failed to create payment link: ${e.message}"))
        }
    }
}