package com.aymanegeek.imedia.payment.infra.config

import com.aymanegeek.imedia.order.application.usecase.UpdateOrderPaymentStatusUseCase
import com.aymanegeek.imedia.payment.application.StripeWebhookHandler
import com.aymanegeek.imedia.payment.domain.PaymentProcessor
import com.aymanegeek.imedia.payment.infra.stripe.StripePaymentProcessor
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