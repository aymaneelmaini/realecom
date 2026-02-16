package com.aymanegeek.imedia.payment.infra.config

import com.stripe.Stripe
import jakarta.annotation.PostConstruct
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
class StripeConfiguration(
    private val configurationProperties: StripeConfigurationProperties
) {

    @PostConstruct
    fun initStripe() {
        Stripe.apiKey = configurationProperties.secretKey
    }
}

@ConfigurationProperties("app.stripe")
data class StripeConfigurationProperties(
    val secretKey: String,
    val webhookSecret: String
)