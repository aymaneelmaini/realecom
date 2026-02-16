package com.aymanegeek.imedia

import com.aymanegeek.imedia.payment.infra.config.StripeConfigurationProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableTransactionManagement
@EnableConfigurationProperties(StripeConfigurationProperties::class)
class ImediaApplication

fun main(args: Array<String>) {
    runApplication<ImediaApplication>(*args)
}
