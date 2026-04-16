package com.newonedev.easymart.payment.infra.config

import com.newonedev.easymart.order.application.usecase.UpdateOrderPaymentStatusUseCase
import com.newonedev.easymart.payment.application.StripeWebhookHandler
import com.newonedev.easymart.payment.domain.PaymentProcessor
import com.newonedev.easymart.payment.infra.stripe.StripePaymentProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PaymentConfig {

    @Bean
    fun paymentProcessor(): PaymentProcessor = StripePaymentProcessor()

    @Bean
    fun stripeWebhookHandler(
        updateOrderPaymentStatusUseCase: UpdateOrderPaymentStatusUseCase,
        configurationProperties: StripeConfigurationProperties
    ): StripeWebhookHandler = StripeWebhookHandler(
        updateOrderPaymentStatusUseCase,
        configurationProperties
    )
}