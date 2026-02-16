package com.aymanegeek.imedia.payment.infra.stripe.webhook

import com.aymanegeek.imedia.order.application.usecase.UpdateOrderPaymentStatusRequest
import com.aymanegeek.imedia.order.application.usecase.UpdateOrderPaymentStatusUseCase
import com.aymanegeek.imedia.order.domain.OrderId
import com.aymanegeek.imedia.payment.infra.stripe.StripeConfigurationProperties
import com.stripe.exception.SignatureVerificationException
import com.stripe.model.Event
import com.stripe.model.checkout.Session
import com.stripe.net.Webhook
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/webhooks/stripe")
class StripeWebhook(
    private val updateOrderPaymentStatusUseCase: UpdateOrderPaymentStatusUseCase,
    private val configurationProperties: StripeConfigurationProperties
) {

    private val logger = LoggerFactory.getLogger(StripeWebhook::class.java)

    @PostMapping
    fun handleWebhook(
        @RequestBody payload: String,
        @RequestHeader("Stripe-Signature") signature: String
    ): ResponseEntity<String> {
        val event: Event = try {
            Webhook.constructEvent(payload, signature, configurationProperties.webhookSecret)
        } catch (e: SignatureVerificationException) {
            logger.error("Invalid webhook signature", e)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature")
        }

        logger.info("Received Stripe webhook event: ${event.type}")

        when (event.type) {
            "checkout.session.completed" -> handleCheckoutSessionCompleted(event)
            "checkout.session.expired" -> handleCheckoutSessionExpired(event)
            else -> logger.info("Unhandled event type: ${event.type}")
        }

        return ResponseEntity.ok("Webhook processed")
    }

    private fun handleCheckoutSessionCompleted(event: Event) {
        val session = event.dataObjectDeserializer.`object`.orElse(null) as? Session
        if (session == null) {
            logger.error("Failed to deserialize checkout session from event")
            return
        }

        val orderId = session.metadata["order_id"]
        if (orderId == null) {
            logger.error("No order_id found in session metadata")
            return
        }

        logger.info("Payment successful for order: $orderId")

        updateOrderPaymentStatusUseCase.execute(
            UpdateOrderPaymentStatusRequest(
                orderId = OrderId(UUID.fromString(orderId)),
                isPaid = true,
                externalPaymentId = session.id
            )
        ).fold(
            ifLeft = { error -> logger.error("Failed to update order status: ${error.message}") },
            ifRight = { logger.info("Order $orderId status updated to PAID") }
        )
    }

    private fun handleCheckoutSessionExpired(event: Event) {
        val session = event.dataObjectDeserializer.`object`.orElse(null) as? Session
        if (session == null) {
            logger.error("Failed to deserialize checkout session from event")
            return
        }

        val orderId = session.metadata["order_id"]
        if (orderId == null) {
            logger.error("No order_id found in session metadata")
            return
        }

        logger.info("Payment expired for order: $orderId")

        updateOrderPaymentStatusUseCase.execute(
            UpdateOrderPaymentStatusRequest(
                orderId = OrderId(UUID.fromString(orderId)),
                isPaid = false,
                externalPaymentId = session.id
            )
        ).fold(
            ifLeft = { error -> logger.error("Failed to update order status: ${error.message}") },
            ifRight = { logger.info("Order $orderId status updated to FAILED") }
        )
    }
}