package com.aymanegeek.imedia.payment.infra.stripe.webhook

import com.aymanegeek.imedia.payment.application.service.StripeWebhookHandler
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/webhooks/stripe")
class StripeWebhookController(
    private val webhookHandler: StripeWebhookHandler
) {

    @PostMapping
    fun handleWebhook(
        @RequestBody payload: String,
        @RequestHeader("Stripe-Signature") signature: String
    ): ResponseEntity<String> =
        webhookHandler.handleWebhookEvent(payload, signature).fold(
            ifLeft = { error ->
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error.message)
            },
            ifRight = {
                ResponseEntity.ok("Webhook processed")
            }
        )
}