package com.aymanegeek.imedia.payment.application.service

import arrow.core.Either
import com.aymanegeek.imedia.order.application.usecase.UpdateOrderPaymentStatusRequest
import com.aymanegeek.imedia.order.application.usecase.UpdateOrderPaymentStatusUseCase
import com.aymanegeek.imedia.order.domain.OrderId
import com.aymanegeek.imedia.payment.domain.WebhookError
import com.aymanegeek.imedia.payment.infra.stripe.StripeConfigurationProperties
import com.stripe.exception.SignatureVerificationException
import com.stripe.model.Event
import com.stripe.model.checkout.Session
import com.stripe.net.Webhook
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class StripeWebhookHandler(
    private val updateOrderPaymentStatusUseCase: UpdateOrderPaymentStatusUseCase,
    private val configurationProperties: StripeConfigurationProperties
) {

    private val logger = LoggerFactory.getLogger(StripeWebhookHandler::class.java)

    fun handleWebhookEvent(payload: String, signature: String): Either<WebhookError, Unit> {
        val event = verifyWebhookSignature(payload, signature)
            ?: return Either.Left(WebhookError.InvalidSignature)

        logger.info("Received Stripe webhook event: ${event.type}")

        return when (event.type) {
            "checkout.session.completed" -> processCheckoutSession(event, isPaid = true)
            "checkout.session.expired" -> processCheckoutSession(event, isPaid = false)
            else -> {
                logger.info("Unhandled event type: ${event.type}")
                Either.Right(Unit)
            }
        }
    }

    private fun verifyWebhookSignature(payload: String, signature: String): Event? =
        try {
            Webhook.constructEvent(payload, signature, configurationProperties.webhookSecret)
        } catch (e: SignatureVerificationException) {
            logger.error("Invalid webhook signature", e)
            null
        }

    private fun processCheckoutSession(event: Event, isPaid: Boolean): Either<WebhookError, Unit> {
        val session = extractSession(event)
            ?: return Either.Left(WebhookError.InvalidEventData("Failed to deserialize checkout session"))

        val orderId = extractOrderId(session)
            ?: return Either.Left(WebhookError.InvalidEventData("No order_id found in session metadata"))

        val status = if (isPaid) "successful" else "expired"
        logger.info("Payment $status for order: $orderId")

        return updateOrderPaymentStatus(orderId, session.id, isPaid)
    }

    private fun extractSession(event: Event): Session? {
        val session = event.dataObjectDeserializer.`object`.orElse(null) as? Session
        if (session == null) {
            logger.error("Failed to deserialize checkout session from event")
        }
        return session
    }

    private fun extractOrderId(session: Session): String? {
        val orderId = session.metadata["order_id"]
        if (orderId == null) {
            logger.error("No order_id found in session metadata")
        }
        return orderId
    }

    private fun updateOrderPaymentStatus(
        orderId: String,
        sessionId: String,
        isPaid: Boolean
    ): Either<WebhookError, Unit> {
        val targetStatus = if (isPaid) "PAID" else "FAILED"

        return updateOrderPaymentStatusUseCase.execute(
            UpdateOrderPaymentStatusRequest(
                orderId = OrderId(UUID.fromString(orderId)),
                isPaid = isPaid,
                externalPaymentId = sessionId
            )
        ).mapLeft { error ->
            logger.error("Failed to update order status: ${error.message}")
            WebhookError.OrderUpdateFailed(error.message)
        }.map {
            logger.info("Order $orderId status updated to $targetStatus")
        }
    }
}
